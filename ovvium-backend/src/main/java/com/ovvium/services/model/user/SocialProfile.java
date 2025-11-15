package com.ovvium.services.model.user;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

import static com.ovvium.services.util.ovvium.base.Preconditions.checkNotBlank;

@Getter
@Embeddable
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class SocialProfile {

	private String id;
	private String email;
	private String fullName;

	public SocialProfile(String id, String email, String fullName) {
		this.id = checkNotBlank(id, "Id cannot be blank");
		this.email = checkNotBlank(email, "Email cannot be blank");
		this.fullName = checkNotBlank(fullName, "Full name cannot be blank");
	}

}
