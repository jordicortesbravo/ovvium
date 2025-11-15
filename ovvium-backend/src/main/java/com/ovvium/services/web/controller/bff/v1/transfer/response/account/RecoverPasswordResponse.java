package com.ovvium.services.web.controller.bff.v1.transfer.response.account;

import com.ovvium.services.web.controller.bff.v1.transfer.response.user.UserResponse;
import lombok.Data;

@Data
public class RecoverPasswordResponse {

	private final UserResponse user;

	public RecoverPasswordResponse(UserResponse userResponse) {
		this.user = userResponse;
	}
}
