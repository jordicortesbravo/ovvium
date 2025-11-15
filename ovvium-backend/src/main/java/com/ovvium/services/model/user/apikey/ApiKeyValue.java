package com.ovvium.services.model.user.apikey;

import com.ovvium.services.util.ovvium.string.OvviumStringUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

import static com.ovvium.services.util.ovvium.base.Preconditions.check;
import static com.ovvium.services.util.ovvium.base.Preconditions.checkNotBlank;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = PROTECTED)
public final class ApiKeyValue {

	private final static int API_KEY_LENGTH = 32;

	private String key;

	private ApiKeyValue(String value) {
		this.key = check(checkNotBlank(value, "Key cannot be blank"), value.length() == API_KEY_LENGTH, "Key length is not correct");
	}

	public static ApiKeyValue ofKey(String value) {
		return new ApiKeyValue(value);
	}

	public static ApiKeyValue randomKey() {
		return new ApiKeyValue(OvviumStringUtils.randomAlphanumeric(API_KEY_LENGTH));
	}

}
