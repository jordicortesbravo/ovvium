package com.ovvium.services.repository.client.payment.dto;

import com.ovvium.services.repository.client.payment.ws.dto.RemoveUserTokenWsResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RemoveUserTokenResponse {

	private Integer response;

	public RemoveUserTokenResponse(RemoveUserTokenWsResponse response) {
		this.response = response.response();
	}

}
