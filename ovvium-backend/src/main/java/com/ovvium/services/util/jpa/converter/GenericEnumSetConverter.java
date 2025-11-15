package com.ovvium.services.util.jpa.converter;

import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;

import javax.persistence.AttributeConverter;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Note: Concrete class needs to also implements AttributeConverter.
 */
public abstract class GenericEnumSetConverter<E extends Enum<E>> implements AttributeConverter<Set<E>, String> {

	private static final String SPLIT_CHAR = ";";

	private final Class<E> clazz;

	protected GenericEnumSetConverter(Class<E> clazz) {
		this.clazz = clazz;
	}

	@Override
	public String convertToDatabaseColumn(Set<E> values) {
		if (values.isEmpty()) {
			return null;
		}
		return values.stream()
				.map(Enum::name)
				.collect(Collectors.joining(SPLIT_CHAR));
	}

	@Override
	public Set<E> convertToEntityAttribute(String string) {
		if (StringUtils.isBlank(string)) {
			return EnumSet.noneOf(clazz);
		}
		List<E> values = Stream.of(string.split(SPLIT_CHAR))
				.map(e -> Enum.valueOf(clazz, e))
				.collect(Collectors.toList());
		return Sets.newEnumSet(values, clazz);
	}

}