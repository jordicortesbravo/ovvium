package com.ovvium.services.repository.client.social;

import com.ovvium.services.model.user.SocialProvider;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.net.URI;
import java.util.Optional;

@Data
@EqualsAndHashCode(of = {"provider", "id"})
public class SocialProfileDto {

	private final SocialProvider provider;
	private String id;
	private String email;
	private String fullName;
	private URI profileImage;

	public Optional<String> getId() {
		return Optional.ofNullable(id);
	}

	public Optional<String> getEmail() {
		return Optional.ofNullable(email);
	}

	public Optional<String> getFullName() {
		return Optional.ofNullable(fullName);
	}

	public Optional<URI> getProfileImage() {
		return Optional.ofNullable(profileImage);
	}
}
