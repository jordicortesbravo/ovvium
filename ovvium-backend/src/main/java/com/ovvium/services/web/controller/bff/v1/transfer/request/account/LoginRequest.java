package com.ovvium.services.web.controller.bff.v1.transfer.request.account;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class LoginRequest {

	private String email;
	private String password;

}
