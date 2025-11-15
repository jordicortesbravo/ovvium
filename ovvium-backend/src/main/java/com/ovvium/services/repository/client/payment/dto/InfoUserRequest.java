package com.ovvium.services.repository.client.payment.dto;

import com.ovvium.services.model.user.User;
import com.ovvium.services.model.user.UserPciDetails;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class InfoUserRequest {

	private final User user;
	private final UserPciDetails pciDetails;

}
