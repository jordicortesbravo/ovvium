package com.ovvium.services.model.user.event;

import com.ovvium.services.model.user.User;
import com.ovvium.services.util.ovvium.domain.event.AbstractOvviumEvent;
import lombok.Data;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@Data
public final class UserSocialLoggedEvent extends AbstractOvviumEvent {

	private final UUID userId;
	private URI profileImage;

	public UserSocialLoggedEvent(User user) {
		this.userId = user.getId();
	}

	public Optional<URI> getProfileImage() {
		return Optional.ofNullable(profileImage);
	}
}
