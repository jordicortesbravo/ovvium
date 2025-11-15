package com.ovvium.services.model.user.apikey;

import com.ovvium.services.util.ovvium.base.Preconditions;
import com.ovvium.services.util.ovvium.domain.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.Embedded;
import javax.persistence.Entity;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
@Accessors(chain = true)
public class ApiKey extends BaseEntity {

	@Embedded
	private ApiKeyValue key;

	private String client;

	public ApiKey(ApiKeyValue apiKey, String client) {
		this.key = Preconditions.checkNotNull(apiKey, "Api Key cannot be blank");
		this.client = Preconditions.checkNotBlank(client, "Client cannot be blank");
	}

}
