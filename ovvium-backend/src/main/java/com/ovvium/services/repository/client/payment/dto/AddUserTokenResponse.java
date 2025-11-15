package com.ovvium.services.repository.client.payment.dto;

import com.ovvium.services.repository.client.payment.ws.dto.AddUserTokenWsResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddUserTokenResponse {

	private final String userId;
	private final String userToken;

	public AddUserTokenResponse(AddUserTokenWsResponse response) {
		this.userId = response.userId();
		this.userToken = response.userToken();
	}

}
