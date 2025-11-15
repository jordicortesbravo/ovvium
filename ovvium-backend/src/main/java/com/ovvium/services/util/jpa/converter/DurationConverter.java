package com.ovvium.services.util.jpa.converter;

import java.time.Duration;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class DurationConverter implements AttributeConverter<Duration, String> {

	@Override
	public String convertToDatabaseColumn(Duration duration) {
		return duration == null ? null : duration.toString();
	}

	@Override
	public Duration convertToEntityAttribute(String duration) {
		return duration == null ? null : Duration.parse(duration);
	}
}