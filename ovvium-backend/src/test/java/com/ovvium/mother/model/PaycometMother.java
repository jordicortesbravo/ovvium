package com.ovvium.mother.model;

import com.ovvium.services.model.common.MoneyAmount;
import com.ovvium.services.repository.client.payment.dto.ExecutePurchaseResponse;
import com.ovvium.services.repository.client.payment.dto.InfoUserResponse;
import com.ovvium.services.repository.client.payment.dto.SplitTransferResponse;
import com.ovvium.services.repository.client.payment.ws.dto.AddUserTokenWsResponse;
import com.ovvium.services.repository.client.payment.ws.dto.ExecutePurchaseWsResponse;
import com.ovvium.services.repository.client.payment.ws.dto.InfoUserWsResponse;
import com.ovvium.services.repository.client.payment.ws.dto.SplitTransferWsResponse;
import lombok.experimental.UtilityClass;

import java.net.URI;
import java.util.UUID;

@UtilityClass
public class PaycometMother {

	public static final URI ANY_CHALLENGE_URL = URI.create("https://paycomet.com/challenge_url");

	public static AddUserTokenWsResponse anyCorrectAddUserTokenWsResponse() {
		return new AddUserTokenWsResponse()
				.userId("user-id")
				.userToken("user-token");
	}

	public static InfoUserWsResponse anyCorrectInfoUserWsResponse() {
		InfoUserWsResponse wsResponse = new InfoUserWsResponse();
		wsResponse.setCardBrand("VISA");
		wsResponse.setCardCategory("BUSINESS");
		wsResponse.setCardCountry("ESP");
		wsResponse.setCardExpiryDate("2021/05");
		wsResponse.setCardType("CREDIT");
		wsResponse.setCardSepa(1);
		wsResponse.setCardPan("123456-XXX-XXX-123");
		wsResponse.setCardHash("hash");
		return wsResponse;
	}

	public static InfoUserResponse anyInfoUserResponse() {
		return new InfoUserResponse(anyCorrectInfoUserWsResponse());
	}

	public static ExecutePurchaseWsResponse executePurchaseWsResponse(MoneyAmount amount) {
		ExecutePurchaseWsResponse wsResponse = new ExecutePurchaseWsResponse();
		wsResponse.setAmount(amount.asInt());
		wsResponse.setResponse(1);
		wsResponse.setAuthCode("authcode");
		wsResponse.setOrder(UUID.randomUUID().toString());
		wsResponse.setCurrency(amount.getCurrency().getCurrencyCode());
		return wsResponse;
	}

	public static ExecutePurchaseResponse executePurchaseResponse(MoneyAmount amount) {
		return new ExecutePurchaseResponse(executePurchaseWsResponse(amount));
	}

	public static ExecutePurchaseResponse executePurchaseWithChallengeUrlResponse(MoneyAmount amount) {
		ExecutePurchaseWsResponse response = executePurchaseWsResponse(amount);
		response.setChallengeUrl(ANY_CHALLENGE_URL.toString());
		response.setAuthCode(null);
		return new ExecutePurchaseResponse(response);
	}

	public static SplitTransferWsResponse splitTransferWsResponse(MoneyAmount amount) {
		SplitTransferWsResponse wsResponse = new SplitTransferWsResponse();
		wsResponse.setSubmerchantAmount(amount.asInt());
		wsResponse.setResponse(1);
		wsResponse.setTransferAuthCode("authcode");
		wsResponse.setOrder(UUID.randomUUID().toString());
		wsResponse.setSubmerchantCurrency(amount.getCurrency().getCurrencyCode());
		return wsResponse;
	}

	public static SplitTransferResponse splitTransferResponse(MoneyAmount amount) {
		return new SplitTransferResponse(splitTransferWsResponse(amount));
	}

}
