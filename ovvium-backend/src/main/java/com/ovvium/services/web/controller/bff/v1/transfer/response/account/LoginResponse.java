package com.ovvium.services.web.controller.bff.v1.transfer.response.account;

import com.ovvium.services.web.controller.bff.v1.transfer.response.user.UserCustomerResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.user.UserResponse;
import lombok.Data;

@Data
public final class LoginResponse {

	private final UserResponse user;
	private final SessionResponse session;

	public LoginResponse(UserResponse user, SessionResponse session) {
		this.user = user;
		this.session = session;
	}

	public LoginResponse(UserCustomerResponse userCustomerResponse, SessionResponse session) {
		this.user = userCustomerResponse;
		this.session = session;
	}

	public LoginResponse(UserResponse user) {
		this(user, null);
	}
}
