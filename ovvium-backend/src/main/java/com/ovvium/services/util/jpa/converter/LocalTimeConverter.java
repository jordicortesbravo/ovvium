package com.ovvium.services.util.jpa.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Time;
import java.time.LocalTime;

@Converter(autoApply = true)
public class LocalTimeConverter implements AttributeConverter<LocalTime, Time> {

	@Override
	public Time convertToDatabaseColumn(LocalTime time) {
		return time == null ? null : Time.valueOf(time);
	}

	@Override
	public LocalTime convertToEntityAttribute(Time time) {
		return time == null ? null : time.toLocalTime();
	}

	public static LocalTime convert(Time date) {
		return new LocalTimeConverter().convertToEntityAttribute(date);
	}

	public static Time convert(LocalTime localTime) {
		return new LocalTimeConverter().convertToDatabaseColumn(localTime);
	}
}