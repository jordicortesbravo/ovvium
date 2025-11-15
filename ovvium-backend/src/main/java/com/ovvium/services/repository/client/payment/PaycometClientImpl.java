package com.ovvium.services.repository.client.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ovvium.services.app.config.properties.PaycometProperties;
import com.ovvium.services.model.bill.Order;
import com.ovvium.services.model.payment.UnsuccessfulPaymentClientException;
import com.ovvium.services.model.product.Product;
import com.ovvium.services.model.user.User;
import com.ovvium.services.repository.client.payment.dto.*;
import com.ovvium.services.repository.client.payment.exception.PaycometException;
import com.ovvium.services.repository.client.payment.ws.PaycometWsClient;
import com.ovvium.services.repository.client.payment.ws.dto.*;
import com.ovvium.services.util.ovvium.encoding.EncodingUtils;
import com.ovvium.services.util.ovvium.spring.SpringRequestUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.net.URLEncoder;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
public class PaycometClientImpl implements PaymentClient {

	private static final Pattern IPV4_PATTERN = Pattern.compile("\\b((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(\\.|$)){4}\\b");
	private static final String DEFAULT_IPV4_IP = "0.0.0.1";

	private final PaycometWsClient client;
	private final Map<Integer, String> paycometErrorsMap;
	private final String terminal;
	private final String merchantCode;
	private final String password;
	private final String jetId;
	private final ObjectMapper objectMapper;

	public PaycometClientImpl(PaycometWsClient client, PaycometProperties paycometProperties, Map<Integer, String> paycometErrorsMap, ObjectMapper objectMapper) {
		this.client = client;
		this.terminal = paycometProperties.getTerminal();
		this.merchantCode = paycometProperties.getCode();
		this.password = paycometProperties.getPassword();
		this.jetId = paycometProperties.getJetId();
		this.paycometErrorsMap = paycometErrorsMap;
		this.objectMapper = objectMapper;
	}

	@Override
	public AddUserTokenResponse addUserToken(AddUserTokenRequest request) {
		val wsRequest = new AddUserTokenWsRequest()
				.setMerchantCode(merchantCode)
				.setTerminal(terminal)
				.setJetToken(request.jetToken())
				.setJetId(jetId)
				.setOriginalIp(getOriginalIpAddress())
				.setSignature(
						PaycometSignatureFactory.addUserToken(merchantCode, request.jetToken(), jetId, terminal, password)
				);
		val wsResponse = client.addUserToken(wsRequest);
		checkForErrors(new CheckClientErrorsRequest(wsResponse.errorId(), null, false, wsResponse));
		log.info("Added user token to Paycomet for user {}", request.user().getId());
		return new AddUserTokenResponse(wsResponse);
	}

	@Override
	public RemoveUserTokenResponse removeUserToken(RemoveUserTokenRequest request) {
		val wsRequest = new RemoveUserTokenWsRequest()
				.setMerchantCode(merchantCode)
				.setTerminal(terminal)
				.setUserId(request.userId())
				.setUserToken(request.userToken())
				.setOriginalIp(getOriginalIpAddress())
				.setSignature(
						PaycometSignatureFactory.removeUserToken(merchantCode, request.userId(), request.userToken(), terminal, password)
				);
		val wsResponse = client.removeUserToken(wsRequest);
		checkForErrors(new CheckClientErrorsRequest(wsResponse.errorId(), wsResponse.response(), true, wsResponse));
		log.info("Removed user token from Paycomet for user {}", request.user().getId());
		return new RemoveUserTokenResponse(wsResponse);
	}

	@Override
	public ExecutePurchaseResponse executePurchase(ExecutePurchaseRequest request) {
		var purchaseAmount = request.amount();
		var userPciDetails = request.userPciDetails();
		var stringAmount = String.valueOf(purchaseAmount.asInt());
		val wsRequest = new ExecutePurchaseWsRequest()
				.setMerchantCode(merchantCode)
				.setTerminal(terminal)
				.setUserId(userPciDetails.getProviderUserId())
				.setUserToken(userPciDetails.getProviderReferenceToken())
				.setAmount(stringAmount)
				.setOrder(request.orderId().toString())
				.setCurrency(purchaseAmount.getCurrency().getCurrencyCode())
				.setProductDescription(getProductIds(request)) // For logging purposes
				.setOriginalIp(getOriginalIpAddress())
				.setUserInteraction(1) // for PSD2
				.setMerchantData(getEncryptedMerchantData(request.user()))
				.setScaException(purchaseAmount.isLessOrEqualThan(purchaseAmount.withAmount(30)) ? "LWV" : null)
				.setSignature(
						PaycometSignatureFactory.executePurchase(merchantCode, userPciDetails.getProviderUserId(), userPciDetails.getProviderReferenceToken(), terminal, stringAmount, request.orderId().toString(), password)
				);
		val wsResponse = client.executePurchase(wsRequest);
		checkForErrors(new CheckClientErrorsRequest(wsResponse.errorId(), wsResponse.getResponse(), false, wsResponse));
		var purchaseResponse = new ExecutePurchaseResponse(wsResponse);
		log.info("ExecutePurchase with Order Id {} for Customer {} of {} amount. Pending confirmation: {}", request.orderId(), request.customerId(), purchaseAmount, purchaseResponse.getChallengeUrl().isPresent());
		return purchaseResponse;
	}

	@SneakyThrows
	private String getEncryptedMerchantData(User user) {
		// https://docs.paycomet.com/es/documentacion/psd2-parametros
		val data = objectMapper.writeValueAsString(new PaycometUserMerchantData(user));
		return URLEncoder.encode(EncodingUtils.b64(data, UTF_8), UTF_8);
	}

	@Override
	public InfoUserResponse getInfoUser(InfoUserRequest request) {
		var userPciDetails = request.pciDetails();
		val wsRequest = new InfoUserWsRequest()
				.setMerchantCode(merchantCode)
				.setTerminal(terminal)
				.setIdUser(userPciDetails.getProviderUserId())
				.setTokenUser(userPciDetails.getProviderReferenceToken())
				.setOriginalIp(getOriginalIpAddress())
				.setSignature(
						PaycometSignatureFactory.getUserInfo(merchantCode, userPciDetails.getProviderUserId(), userPciDetails.getProviderReferenceToken(), terminal, password)
				);
		val wsResponse = client.getInfoUser(wsRequest);
		checkForErrors(new CheckClientErrorsRequest(wsResponse.errorId(), null, false, wsResponse));
		return new InfoUserResponse(wsResponse);
	}

	@Override
	public SplitTransferResponse splitTransfer(SplitTransferRequest request) {
		String amount = String.valueOf(request.submerchantAmount().asInt());
		String currency = request.submerchantAmount().getCurrency().getCurrencyCode();
		val wsRequest = new SplitTransferWsRequest()
				.setMerchantCode(merchantCode)
				.setTerminal(terminal)
				.setAuthCode(request.authCode())
				.setOrder(request.order().toString())
				.setSubmerchantAmount(amount)
				.setSubmerchantCurrency(currency)
				.setSubmerchantSplitId(request.submerchantSplitId())
				.setSignature(
						PaycometSignatureFactory.splitTransfer(merchantCode, terminal, request.order().toString(), request.authCode(),
								request.submerchantSplitId(), amount, currency, password)
				);
		val wsResponse = client.splitTransfer(wsRequest);
		checkForErrors(new CheckClientErrorsRequest(wsResponse.errorId(), wsResponse.getResponse(), true, wsResponse));
		log.info("Executed split transfer for Customer {} of {} amount", request.customer().getId(), request.submerchantAmount());
		return new SplitTransferResponse(wsResponse);
	}

	@Override
	public boolean verifyNotification(VerifyNotificationRequest request) {
		var isSamehash = request.hash().equals(PaycometSignatureFactory.notificationHash(merchantCode, terminal, password, request));
		if (!isSamehash) {
			log.error("Notification Hash does not match.");
		}
		return isSamehash;
	}

	/**
	 * Check for errors from incoming responses.
	 * The 'response' field is sent in some of the responses.
	 * A response == 1 means the operation is completed, but a response == 0 does not necessarily mean it is an error.
	 * If failOnPendingResponse is true, we expect the operation response sent as completed, otherwise we throw an exception.
	 */
	@Override
	public void checkForErrors(CheckClientErrorsRequest request) throws UnsuccessfulPaymentClientException {
		request.getErrorId()
				.map(Integer::parseInt)
				.ifPresent((errorId) -> {
					throw new PaycometException(errorId, paycometErrorsMap.getOrDefault(errorId, "Unexpected error"), request.response());
				});
		if (request.failOnWrongResponseId()) {
			request.getResponseId()
					.filter(r -> r == 1)
					.orElseThrow(() -> new PaycometException("Wrong Response value", request.response()));
		}
	}

	private String getProductIds(ExecutePurchaseRequest request) {
		return request.orders().stream()
				.map(Order::getProduct)
				.map(Product::getId)
				.map(Object::toString)
				.collect(Collectors.joining(","));
	}

	/**
	 * Gets an IPV4 address only if matches the IPV4 format. Default otherwise.
	 * Paycomet wants IP only in IPv4 format. This is just for logging purposes.
	 */
	private String getOriginalIpAddress() {
		return SpringRequestUtils.getOriginalIpAddress()
				.filter(ip -> IPV4_PATTERN.matcher(ip).matches())
				.orElseGet(() -> {
					log.warn("No original IP found for current request");
					return DEFAULT_IPV4_IP;
				});
	}

}
