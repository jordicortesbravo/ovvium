package com.ovvium.services.util.jpa.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Converter(autoApply = true)
public class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, Timestamp> {

	@Override
	public Timestamp convertToDatabaseColumn(LocalDateTime time) {
		return time == null ? null : Timestamp.valueOf(time);
	}

	@Override
	public LocalDateTime convertToEntityAttribute(Timestamp date) {
		return date == null ? null : date.toLocalDateTime();
	}

	public static LocalDateTime convert(Timestamp date) {
		return new LocalDateTimeConverter().convertToEntityAttribute(date);
	}

	public static Timestamp convert(LocalDateTime localDateTime) {
		return new LocalDateTimeConverter().convertToDatabaseColumn(localDateTime);
	}
}