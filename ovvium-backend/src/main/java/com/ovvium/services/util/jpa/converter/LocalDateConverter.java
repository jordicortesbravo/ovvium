package com.ovvium.services.util.jpa.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Date;
import java.time.LocalDate;

@Converter(autoApply = true)
public class LocalDateConverter implements AttributeConverter<LocalDate, Date> {

	@Override
	public Date convertToDatabaseColumn(LocalDate date) {
		return date == null ? null : Date.valueOf(date);
	}

	@Override
	public LocalDate convertToEntityAttribute(Date date) {
		return date == null ? null : date.toLocalDate();
	}
	
	public static LocalDate convert(Date date) {
		return new LocalDateConverter().convertToEntityAttribute(date);
	}
	
	public static Date convert(LocalDate localDate) {
		return new LocalDateConverter().convertToDatabaseColumn(localDate);
	}
}