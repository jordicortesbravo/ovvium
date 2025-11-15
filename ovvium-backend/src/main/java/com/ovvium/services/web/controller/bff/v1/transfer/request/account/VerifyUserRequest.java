package com.ovvium.services.web.controller.bff.v1.transfer.request.account;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@RequiredArgsConstructor
@Accessors(chain = true)
public class VerifyUserRequest {

	@NotNull
	private final UUID userId;
	@NotEmpty
	private final String activationCode;

}
