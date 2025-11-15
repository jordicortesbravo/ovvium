package com.ovvium.services.web.controller.bff.v1.transfer.response.user;

import com.ovvium.services.model.user.User;
import com.ovvium.services.web.controller.bff.v1.transfer.response.common.ResourceIdResponse;
import lombok.Getter;

import java.net.URI;

@Getter
public class UserResponse extends ResourceIdResponse {

	private final String name;
	private final String email;
	private final boolean enabled;
	private final URI imageUri;

	public UserResponse(User user, URI pictureUri) {
		super(user);
		this.name = user.getName();
		this.email = user.getEmail();
		this.enabled = user.isEnabled();
		this.imageUri = pictureUri;
	}

}
