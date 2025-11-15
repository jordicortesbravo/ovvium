package com.ovvium.services.repository.client.payment.dto;

import com.ovvium.services.model.user.User;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Data
@RequiredArgsConstructor
@Accessors(fluent = true)
public class AddUserTokenRequest {

	private final String jetToken;
	private final User user;

}
