package com.ovvium.services.util.jpa.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

// Don´t use autoapply here by default! Use @Convert(converter = UuidSetConverter.class) when needed.
@Converter
public class UuidSetConverter implements AttributeConverter<Set<UUID>, String> {
    private static final String SPLIT_CHAR = ";";

    @Override
    public String convertToDatabaseColumn(Set<UUID> uuidList) {
        return uuidList == null ? null : uuidList.stream()
                .map(UUID::toString)
                .collect(Collectors.joining(SPLIT_CHAR));
    }

    @Override
    public Set<UUID> convertToEntityAttribute(String string) {
        return string == null ? null : Arrays.stream(string.split(SPLIT_CHAR))
                .map(UUID::fromString)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}