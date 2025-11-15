package com.ovvium.services.web.controller.bff.v1.transfer.request.account;

import com.ovvium.services.model.user.SocialProvider;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@NoArgsConstructor
public class AuthorizeRequest {

	private String id;
	private String email;
	private String name;
	private String profileImage;
	private String token;
	private SocialProvider socialProvider;

	public Optional<String> getEmail() {
		return Optional.ofNullable(email);
	}

	public Optional<String> getProfileImage() {
		return Optional.ofNullable(profileImage);
	}

	public Optional<SocialProvider> getSocialProvider() {
		return Optional.ofNullable(socialProvider);
	}
}
