package com.ovvium.services.web.controller.bff.v1.transfer.request.account;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class RegisterRequest {

	private String email;
	private String name;
	private String password;
}
