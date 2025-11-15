package com.ovvium.services.model.user.event;

import com.ovvium.services.model.user.User;
import com.ovvium.services.util.ovvium.domain.event.AbstractOvviumEvent;
import lombok.Data;

import java.util.UUID;

@Data
public final class UserPasswordChangedEvent extends AbstractOvviumEvent {

	private final UUID userId;

	public UserPasswordChangedEvent(User user) {
		this.userId = user.getId();
	}

}
