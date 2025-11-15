package com.ovvium.services.repository.client.payment.dto;

import com.ovvium.services.model.common.MoneyAmount;
import com.ovvium.services.repository.client.payment.ws.dto.ExecutePurchaseWsResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.net.*;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;

@Data
@AllArgsConstructor
public class ExecutePurchaseResponse {

	private final Integer amount;
	private final String order;
	private final String currency;
	private final String authCode;
	private final Integer cardCountry;
	private final Integer response;
	private final URI challengeUrl;

	public ExecutePurchaseResponse(ExecutePurchaseWsResponse response) {
		this.amount = response.getAmount();
		this.order = response.getOrder();
		this.currency = response.getCurrency();
		this.authCode = response.getAuthCode();
		this.cardCountry = response.getCardCountry();
		this.response = response.getResponse();
		this.challengeUrl = setChallengeUrl(response);
	}

	public MoneyAmount getMoneyAmount() {
		return MoneyAmount.ofInteger(amount, currency);
	}

	public Optional<URI> getChallengeUrl() {
		return Optional.ofNullable(challengeUrl);
	}

	// Challenge URL is sent as "0" if empty... If the challengeUrl is not a valid URL, we return Optional.empty
	private URI setChallengeUrl(ExecutePurchaseWsResponse response) {
		return response.getChallengeUrl()
				.map(value -> {
					try {
						var decodedUrl = URLDecoder.decode(value, UTF_8);
						return new URL(decodedUrl).toURI();
					}catch (MalformedURLException | URISyntaxException exc) {
						return null;
					}
				})
				.orElse(null);
	}
}
