package com.ovvium.services.model.user;

import com.ovvium.services.util.ovvium.domain.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.Enumerated;

import static com.ovvium.services.model.user.PciProvider.PAYCOMET;
import static com.ovvium.services.util.ovvium.base.Preconditions.checkNotBlank;
import static javax.persistence.EnumType.STRING;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@Accessors(chain = true)
@NoArgsConstructor(access = PROTECTED)
public class UserPciDetails extends BaseEntity {

	@Enumerated(STRING)
	private PciProvider pciProvider = PAYCOMET;

	private String providerUserId;

	private String providerReferenceToken;

	UserPciDetails(String providerUserId, String providerReferenceToken) {
		this.providerUserId = checkNotBlank(providerUserId, "Provider user id cannot be blank");
		this.providerReferenceToken = checkNotBlank(providerReferenceToken, "Provider reference token cannot be blank");
	}

}
