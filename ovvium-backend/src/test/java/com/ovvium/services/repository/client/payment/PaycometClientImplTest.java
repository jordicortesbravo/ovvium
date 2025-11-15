package com.ovvium.services.repository.client.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ovvium.mother.model.CustomerMother;
import com.ovvium.mother.model.OrderMother;
import com.ovvium.mother.model.UserMother;
import com.ovvium.services.app.config.properties.PaycometProperties;
import com.ovvium.services.model.common.MoneyAmount;
import com.ovvium.services.repository.client.payment.dto.*;
import com.ovvium.services.repository.client.payment.exception.PaycometException;
import com.ovvium.services.repository.client.payment.ws.PaycometWsClient;
import com.ovvium.services.repository.client.payment.ws.dto.*;
import com.ovvium.services.util.util.basic.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

import static com.ovvium.mother.model.CustomerMother.EL_BULLI_CUSTOMER_ID;
import static com.ovvium.mother.model.UserMother.USER_PCI_ID;
import static java.lang.String.format;
import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PaycometClientImplTest {

	private static final String JET_TOKEN = "jet_token";

	private static final String TERMINAL = "1234";
	private static final String MERCHANT_CODE = "merchantcode";
	private static final String PASSWORD = "password";
	private static final String JET_ID = "jetId";
	private static final String AUTH_CODE = "authcode";
	private static final int PAYCOMET_ERROR_ID = 1000;
	private static final String PAYCOMET_ERROR_MSG = "Paycomet error";
	private static final UUID PAYMENT_ORDER_ID = UUID.fromString("ac18b9d6-2490-4a85-a52a-563ae5306dd4");
	private static final String DEFAULT_IP = "0.0.0.1";

	private static final Map<Integer, String> PAYCOMET_ERRORS_MAP = Map.of(PAYCOMET_ERROR_ID, PAYCOMET_ERROR_MSG);

	// SUT
	private PaycometClientImpl client;

	private PaycometWsClient wsClient;

	@BeforeEach
	public void setUp() {
		wsClient = mock(PaycometWsClient.class);
		PaycometProperties props = new PaycometProperties()
				.setCode(MERCHANT_CODE)
				.setJetId(JET_ID)
				.setPassword(PASSWORD)
				.setTerminal(TERMINAL);

		client = new PaycometClientImpl(wsClient, props, PAYCOMET_ERRORS_MAP, new ObjectMapper());
	}

	@Test
	public void given_a_not_correct_request_when_add_user_then_throw_exception() {
		AddUserTokenRequest request = new AddUserTokenRequest(JET_TOKEN, UserMother.getUserJordi());
		AddUserTokenWsResponse wsResponse = new AddUserTokenWsResponse();
		wsResponse.errorId(String.valueOf(PAYCOMET_ERROR_ID));
		when(wsClient.addUserToken(any())).thenReturn(wsResponse);

		assertThatThrownBy(() -> client.addUserToken(request))
				.isInstanceOf(PaycometException.class)
				.hasMessageContaining(format("Error ID %d: %s", PAYCOMET_ERROR_ID, PAYCOMET_ERROR_MSG));
	}

	@Test
	public void given_a_correct_request_when_add_user_token_then_call_add_user_token_ws_request() {
		AddUserTokenRequest request = new AddUserTokenRequest(JET_TOKEN, UserMother.getUserJordi());
		AddUserTokenWsResponse wsResponse = new AddUserTokenWsResponse().userId(USER_PCI_ID);
		when(wsClient.addUserToken(any())).thenReturn(wsResponse);

		client.addUserToken(request);

		AddUserTokenWsRequest capturedRequest = getAddUserTokenCapturedRequest();

		assertThat(capturedRequest.getJetToken()).isEqualTo(JET_TOKEN);
		assertThat(capturedRequest.getOriginalIp()).isEqualTo(DEFAULT_IP);
		assertThat(capturedRequest.getJetId()).isEqualTo(JET_ID);
		assertThat(capturedRequest.getMerchantCode()).isEqualTo(MERCHANT_CODE);
		assertThat(capturedRequest.getTerminal()).isEqualTo(TERMINAL);
		assertThat(capturedRequest.getSignature())
				.isEqualTo("6d5616092a4343b5f18f4dbd27b3f001d0de10268ae6c19ae6b763a98e8a32ec19d41d22b9a01c97e2f712880d9c3b25744501dcb626c8cb5fac80d2ea25dcad");
	}

	@Test
	public void given_a_correct_request_when_remove_user_token_then_call_remove_user_token_ws_request() {
		RemoveUserTokenRequest request = new RemoveUserTokenRequest(UserMother.getUserJordi(), "user-id", "usertoken");
		RemoveUserTokenWsResponse wsResponse = new RemoveUserTokenWsResponse().response(1);
		when(wsClient.removeUserToken(any())).thenReturn(wsResponse);

		client.removeUserToken(request);

		RemoveUserTokenWsRequest capturedRequest = getRemoveUserTokenCapturedRequest();

		assertThat(capturedRequest.getUserId()).isEqualTo(request.userId());
		assertThat(capturedRequest.getUserToken()).isEqualTo(request.userToken());
		assertThat(capturedRequest.getOriginalIp()).isEqualTo(DEFAULT_IP);
		assertThat(capturedRequest.getMerchantCode()).isEqualTo(MERCHANT_CODE);
		assertThat(capturedRequest.getTerminal()).isEqualTo(TERMINAL);
		assertThat(capturedRequest.getSignature())
				.isEqualTo("42e419debf23c30f87eecedc6287105171dfab3b368290895c14828dda5550b2f7c0d56305ee8ad59c30004e2a4b8df16c591f95e2e6ba7152e20e03237a0fdb");
	}

	@Test
	public void given_a_pending_response_when_remove_user_then_throw_exception() {
		RemoveUserTokenRequest request = new RemoveUserTokenRequest(UserMother.getUserJordi(), "user-id", "usertoken");
		RemoveUserTokenWsResponse wsResponse = new RemoveUserTokenWsResponse().response(0);
		when(wsClient.removeUserToken(any())).thenReturn(wsResponse);

		assertThatThrownBy(() -> client.removeUserToken(request))
				.isInstanceOf(PaycometException.class)
				.hasMessageContaining(format("Wrong Response value with response %s", wsResponse));
	}

	@Test
	public void given_a_pending_response_when_check_for_errors_then_throw_exception() {
		RemoveUserTokenWsResponse wsResponse = new RemoveUserTokenWsResponse().response(0);

		assertThatThrownBy(() -> client.checkForErrors(new CheckClientErrorsRequest(
				"0",
				wsResponse.response(),
				true,
				wsResponse
		)))
				.isInstanceOf(PaycometException.class)
				.hasMessageContaining(format("Wrong Response value with response %s", wsResponse));
	}

	@Test
	public void given_error_on_response_when_check_for_errors_then_throw_exception() {
		var wsResponse = new RemoveUserTokenWsResponse().errorId("1");

		assertThatThrownBy(() -> client.checkForErrors(new CheckClientErrorsRequest(
				wsResponse.errorId(),
				null,
				false,
				wsResponse
		)))
				.isInstanceOf(PaycometException.class)
				.hasMessageContaining(format("Error ID 1: Unexpected error with response %s", wsResponse));
	}

	@Test
	public void given_a_correct_request_when_execute_purchase_then_call_execute_purchase_ws_request() {
		var user = UserMother.getUserWithCardData();
		var pciDetails = Utils.first(user.getPciDetails());
		ExecutePurchaseRequest request = new ExecutePurchaseRequest(EL_BULLI_CUSTOMER_ID, user, pciDetails, MoneyAmount.ofDouble(1.0), PAYMENT_ORDER_ID, singleton(OrderMother.getOrderOfCerveza()));
		ExecutePurchaseWsResponse wsResponse = new ExecutePurchaseWsResponse();
		wsResponse.setResponse(1);
		wsResponse.setAmount(100);
		wsResponse.setAuthCode(AUTH_CODE);
		wsResponse.setOrder(PAYMENT_ORDER_ID.toString());
		when(wsClient.executePurchase(any())).thenReturn(wsResponse);

		client.executePurchase(request);

		ExecutePurchaseWsRequest capturedRequest = getExecutePurchaseCapturedRequest();

		assertThat(capturedRequest.getAmount()).isEqualTo("100");
		assertThat(capturedRequest.getUserInteraction()).isEqualTo(1);
		assertThat(capturedRequest.getOriginalIp()).isEqualTo(DEFAULT_IP);
		assertThat(capturedRequest.getCurrency()).isEqualTo("EUR");
		assertThat(capturedRequest.getUserId()).isEqualTo(pciDetails.getProviderUserId());
		assertThat(capturedRequest.getUserToken()).isEqualTo(pciDetails.getProviderReferenceToken());
		assertThat(capturedRequest.getMerchantCode()).isEqualTo(MERCHANT_CODE);
		assertThat(capturedRequest.getTerminal()).isEqualTo(TERMINAL);
		assertThat(capturedRequest.getMerchantData())
				.isEqualTo("eyJjdXN0b21lciI6eyJpZCI6ImE1YmFjOWZlLWQwNGItNDNiOC1hNmQ1LWI5ZGZkODQzZmQ5MCIsIm5hbWUiOiJCaWxsIiwic3VybmFtZSI6IkdhdGVzIiwiZW1haWwiOiJiaWxsLmdhdGVzQGR1bW15LmNvbSJ9fQ%3D%3D");
		assertThat(capturedRequest.getSignature())
				.isEqualTo("30d24b5a48a35f7c6fa6c5327f12b5b1ac2d6c0239c3971dcefa82863f8ec6b3df7bc91906f007fd1754ecd4f78782455b765f1c69d561c66b0be095350e8a3e");
	}

	@Test
	public void given_a_execute_purchase_request_with_user_interaction_when_execute_purchase_with_pending_response_then_should_not_throw_exception() {
		var user = UserMother.getUserWithCardData();
		var pciDetails = Utils.first(user.getPciDetails());
		ExecutePurchaseRequest request = new ExecutePurchaseRequest(EL_BULLI_CUSTOMER_ID, user, pciDetails, MoneyAmount.ofDouble(1.0), UUID.randomUUID(),
				singleton(OrderMother.getOrderOfCerveza()));
		ExecutePurchaseWsResponse wsResponse = new ExecutePurchaseWsResponse();
		wsResponse.setResponse(0);
		when(wsClient.executePurchase(any())).thenReturn(wsResponse);

		client.executePurchase(request);

		ExecutePurchaseWsRequest capturedRequest = getExecutePurchaseCapturedRequest();
		assertThat(capturedRequest.getUserInteraction()).isEqualTo(1);
	}

	@ParameterizedTest
	@CsvSource({"20,LWV", "30,LWV", "31,"})
	public void given_amount_execute_purchase_request_when_execute_purchase_then_should_send_sca_exception_depending_on_amount(double amount, String scaExceptionSent) {
		var user = UserMother.getUserWithCardData();
		var pciDetails = Utils.first(user.getPciDetails());
		ExecutePurchaseRequest request = new ExecutePurchaseRequest(EL_BULLI_CUSTOMER_ID, user, pciDetails, MoneyAmount.ofDouble(amount), UUID.randomUUID(),
				singleton(OrderMother.getOrderOfCerveza()));
		ExecutePurchaseWsResponse wsResponse = new ExecutePurchaseWsResponse();
		wsResponse.setResponse(0);
		when(wsClient.executePurchase(any())).thenReturn(wsResponse);

		client.executePurchase(request);

		ExecutePurchaseWsRequest capturedRequest = getExecutePurchaseCapturedRequest();
		assertThat(capturedRequest.getScaException()).isEqualTo(scaExceptionSent);
	}

	@Test
	public void given_a_not_correct_request_when_execute_purchase_with_errorId_then_should_throw_exception() {
		var user = UserMother.getUserWithCardData();
		var pciDetails = Utils.first(user.getPciDetails());
		var request = new ExecutePurchaseRequest(EL_BULLI_CUSTOMER_ID, user, pciDetails, MoneyAmount.ofDouble(1.0), UUID.randomUUID(),
				singleton(OrderMother.getOrderOfCerveza()));
		var wsResponse = new ExecutePurchaseWsResponse();
		wsResponse.errorId(String.valueOf(PAYCOMET_ERROR_ID));
		when(wsClient.executePurchase(any())).thenReturn(wsResponse);

		assertThatThrownBy(() -> client.executePurchase(request))
				.isInstanceOf(PaycometException.class)
				.hasMessageContaining(format("Error ID %d: %s", PAYCOMET_ERROR_ID, PAYCOMET_ERROR_MSG));
	}

	@Test
	public void given_an_empty_challenge_url_response_when_execute_purchase_then_should_return_empty_url() {
		var user = UserMother.getUserWithCardData();
		var pciDetails = Utils.first(user.getPciDetails());
		var request = new ExecutePurchaseRequest(EL_BULLI_CUSTOMER_ID, user, pciDetails, MoneyAmount.ofDouble(1.0), UUID.randomUUID(),
				singleton(OrderMother.getOrderOfCerveza()));
		var wsResponse = new ExecutePurchaseWsResponse();
		wsResponse.setChallengeUrl("0");
		when(wsClient.executePurchase(any())).thenReturn(wsResponse);

		var executePurchaseResponse = client.executePurchase(request);

		assertThat(executePurchaseResponse.getChallengeUrl()).isEmpty();
	}

	@Test
	public void given_challenge_url_response_when_execute_purchase_then_should_return_decoded_url() {
		var user = UserMother.getUserWithCardData();
		var pciDetails = Utils.first(user.getPciDetails());
		var request = new ExecutePurchaseRequest(EL_BULLI_CUSTOMER_ID, user, pciDetails, MoneyAmount.ofDouble(1.0), UUID.randomUUID(),
				singleton(OrderMother.getOrderOfCerveza()));
		var wsResponse = new ExecutePurchaseWsResponse();
		wsResponse.setChallengeUrl("https%3A%2F%2Fapi.paycomet.com%2Fgateway%2Fsca_challenge.php");
		when(wsClient.executePurchase(any())).thenReturn(wsResponse);

		var executePurchaseResponse = client.executePurchase(request);

		assertThat(executePurchaseResponse.getChallengeUrl()).contains(URI.create("https://api.paycomet.com/gateway/sca_challenge.php"));
	}

	@Test
	public void given_a_correct_request_when_info_user_then_call_info_user_ws_request() {
		var user = UserMother.getUserWithCardData();
		var pciDetails = Utils.first(user.getPciDetails());
		InfoUserRequest request = new InfoUserRequest(user, pciDetails);
		InfoUserWsResponse wsResponse = new InfoUserWsResponse();
		wsResponse.setCardBrand("VISA");
		wsResponse.setCardCategory("BUSINESS");
		wsResponse.setCardCountry("ESP");
		wsResponse.setCardExpiryDate("2021/05");
		wsResponse.setCardType("CREDIT");
		wsResponse.setCardSepa(1);
		wsResponse.setCardPan("123456-XXX-XXX-123");
		wsResponse.setCardHash("hash");
		when(wsClient.getInfoUser(any())).thenReturn(wsResponse);

		client.getInfoUser(request);

		InfoUserWsRequest capturedRequest = getInfoUserCapturedRequest();

		assertThat(capturedRequest.getIdUser()).isEqualTo(pciDetails.getProviderUserId());
		assertThat(capturedRequest.getTokenUser()).isEqualTo(pciDetails.getProviderReferenceToken());
		assertThat(capturedRequest.getOriginalIp()).isEqualTo(DEFAULT_IP);
		assertThat(capturedRequest.getMerchantCode()).isEqualTo(MERCHANT_CODE);
		assertThat(capturedRequest.getTerminal()).isEqualTo(TERMINAL);
		assertThat(capturedRequest.getSignature())
				.isEqualTo("763ffd18cd69351fc1d27212ad9a8030dc6b89d730b15cce7330f825c0d81187300ddb1a5f4371f3323606a4c3c71dcd709800977fd7d6304df35440923f4180");
	}

	@Test
	public void given_a_not_correct_request_when_info_user_with_errorId_then_should_throw_exception() {
		var user = UserMother.getUserWithCardData();
		InfoUserRequest request = new InfoUserRequest(user, Utils.first(user.getPciDetails()));
		InfoUserWsResponse wsResponse = new InfoUserWsResponse();
		wsResponse.errorId(String.valueOf(PAYCOMET_ERROR_ID));

		when(wsClient.getInfoUser(any())).thenReturn(wsResponse);

		assertThatThrownBy(() -> client.getInfoUser(request))
				.isInstanceOf(PaycometException.class)
				.hasMessageContaining(format("Error ID %d: %s", PAYCOMET_ERROR_ID, PAYCOMET_ERROR_MSG));
	}

	@Test
	public void given_a_correct_request_when_split_transfer_then_call_split_transfer_ws_request() {
		MoneyAmount submerchantAmount = MoneyAmount.ofDouble(1.0);
		String currency = submerchantAmount.getCurrency().getCurrencyCode();
		SplitTransferRequest request = new SplitTransferRequest(
				CustomerMother.getCanRocaCustomer(),
				PAYMENT_ORDER_ID,
				"EXECUTE_PURCHASE_AUTHCODE",
				"submerchantSplitId",
				submerchantAmount
		);
		SplitTransferWsResponse wsResponse = new SplitTransferWsResponse();
		wsResponse.setOrder(PAYMENT_ORDER_ID.toString());
		wsResponse.setResponse(1);
		wsResponse.setSubmerchantAmount(100);
		wsResponse.setSubmerchantCurrency(currency);

		when(wsClient.splitTransfer(any())).thenReturn(wsResponse);

		client.splitTransfer(request);

		SplitTransferWsRequest capturedRequest = getSplitTransferCapturedRequest();


		assertThat(capturedRequest.getSubmerchantAmount()).isEqualTo("100");
		assertThat(capturedRequest.getSubmerchantCurrency()).isEqualTo(currency);
		assertThat(capturedRequest.getOrder()).isEqualTo(PAYMENT_ORDER_ID.toString());
		assertThat(capturedRequest.getMerchantCode()).isEqualTo(MERCHANT_CODE);
		assertThat(capturedRequest.getTerminal()).isEqualTo(TERMINAL);
		assertThat(capturedRequest.getSignature())
				.isEqualTo("c6dbc15fdbf890c9a01021da29a51060f283b0c4a8e6941cef8faa3449a883d1");
	}


	@Test
	public void given_a_not_correct_request_when_split_transfer_with_errorId_then_should_throw_exception() {
		UUID orderId = UUID.randomUUID();
		MoneyAmount submerchantAmount = MoneyAmount.ofDouble(1.0);
		SplitTransferRequest request = new SplitTransferRequest(
				CustomerMother.getCanRocaCustomer(),
				orderId,
				"EXECUTE_PURCHASE_AUTHCODE",
				"INCORRECT_SUBMERCHANT_ID",
				submerchantAmount
		);
		SplitTransferWsResponse wsResponse = new SplitTransferWsResponse();
		wsResponse.errorId(String.valueOf(PAYCOMET_ERROR_ID));

		when(wsClient.splitTransfer(any())).thenReturn(wsResponse);

		assertThatThrownBy(() -> client.splitTransfer(request))
				.isInstanceOf(PaycometException.class)
				.hasMessageContaining(format("Error ID %d: %s", PAYCOMET_ERROR_ID, PAYCOMET_ERROR_MSG));
	}

	private AddUserTokenWsRequest getAddUserTokenCapturedRequest() {
		ArgumentCaptor<AddUserTokenWsRequest> argumentCaptor = ArgumentCaptor.forClass(AddUserTokenWsRequest.class);
		verify(wsClient, times(1)).addUserToken(argumentCaptor.capture());
		return argumentCaptor.getValue();
	}

	private RemoveUserTokenWsRequest getRemoveUserTokenCapturedRequest() {
		ArgumentCaptor<RemoveUserTokenWsRequest> argumentCaptor = ArgumentCaptor.forClass(RemoveUserTokenWsRequest.class);
		verify(wsClient, times(1)).removeUserToken(argumentCaptor.capture());
		return argumentCaptor.getValue();
	}

	private ExecutePurchaseWsRequest getExecutePurchaseCapturedRequest() {
		ArgumentCaptor<ExecutePurchaseWsRequest> argumentCaptor = ArgumentCaptor.forClass(ExecutePurchaseWsRequest.class);
		verify(wsClient, times(1)).executePurchase(argumentCaptor.capture());
		return argumentCaptor.getValue();
	}

	private InfoUserWsRequest getInfoUserCapturedRequest() {
		ArgumentCaptor<InfoUserWsRequest> argumentCaptor = ArgumentCaptor.forClass(InfoUserWsRequest.class);
		verify(wsClient, times(1)).getInfoUser(argumentCaptor.capture());
		return argumentCaptor.getValue();
	}

	private SplitTransferWsRequest getSplitTransferCapturedRequest() {
		ArgumentCaptor<SplitTransferWsRequest> argumentCaptor = ArgumentCaptor.forClass(SplitTransferWsRequest.class);
		verify(wsClient, times(1)).splitTransfer(argumentCaptor.capture());
		return argumentCaptor.getValue();
	}
}