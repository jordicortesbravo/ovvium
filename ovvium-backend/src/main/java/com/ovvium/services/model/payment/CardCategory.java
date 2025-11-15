package com.ovvium.services.model.payment;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public enum CardCategory {
	BUSINESS, CONSUMER;

	public static CardCategory safeGet(String value) {
		return Arrays.stream(values())
				.filter(it -> it.name().equalsIgnoreCase(value))
				.findFirst()
				.orElseGet(() -> {
					log.error("Card category not found " + value);
					return CONSUMER;
				});
	}

}