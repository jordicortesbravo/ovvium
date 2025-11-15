package com.ovvium.services.util.jpa.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

// Don´t use autoapply here by default! Use @Convert(converter = StringSetConverter.class) when needed.
@Converter
public class StringSetConverter implements AttributeConverter<Set<String>, String> {
	private static final String SPLIT_CHAR = ";";

	@Override
	public String convertToDatabaseColumn(Set<String> stringList) {
		return String.join(SPLIT_CHAR, stringList);
	}

	@Override
	public Set<String> convertToEntityAttribute(String string) {
		return new LinkedHashSet<>(Arrays.asList(string.split(SPLIT_CHAR)));
	}
}