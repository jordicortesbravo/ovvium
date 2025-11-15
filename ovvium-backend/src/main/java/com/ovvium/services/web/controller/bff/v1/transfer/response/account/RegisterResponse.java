package com.ovvium.services.web.controller.bff.v1.transfer.response.account;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class RegisterResponse {

	private UUID userId;
	private SessionResponse session;
}
