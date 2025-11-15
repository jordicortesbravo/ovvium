package com.ovvium.services.model.product.converter;

import com.ovvium.services.util.jpa.converter.GenericEnumSetConverter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.DayOfWeek;
import java.util.Set;

@Converter
public class DaysOfWeekSetConverter extends GenericEnumSetConverter<DayOfWeek>
		implements AttributeConverter<Set<DayOfWeek>, String> {

	public DaysOfWeekSetConverter() {
		super(DayOfWeek.class);
	}
}
