package com.ovvium.services.repository.client.payment.dto;

import com.ovvium.services.model.user.User;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Data
@RequiredArgsConstructor
@Accessors(fluent = true)
public class RemoveUserTokenRequest {

	private final User user;
	private final String userId;
	private final String userToken;

}
