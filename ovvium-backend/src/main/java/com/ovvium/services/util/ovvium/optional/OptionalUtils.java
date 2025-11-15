package com.ovvium.services.util.ovvium.optional;

import lombok.experimental.UtilityClass;

import java.util.Optional;

import static org.apache.commons.lang.StringUtils.isBlank;

@UtilityClass
public class OptionalUtils {

	public static Optional<String> ofBlankable(String value) {
		return isBlank(value) ? Optional.empty() : Optional.of(value);
	}

}
