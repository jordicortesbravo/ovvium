package com.ovvium.services.security.dto;

import com.ovvium.services.model.user.apikey.ApiKey;
import com.ovvium.services.model.user.apikey.ApiKeyValue;
import lombok.Data;

@Data
public class ApiKeyDto {

	private final ApiKeyValue key;

	public ApiKeyDto(ApiKey apiKey) {
		this.key = apiKey.getKey();
	}

}
