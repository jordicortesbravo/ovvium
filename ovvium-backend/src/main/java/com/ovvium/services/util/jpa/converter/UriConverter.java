package com.ovvium.services.util.jpa.converter;

import lombok.SneakyThrows;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.net.URI;

@Converter(autoApply = true)
public class UriConverter implements AttributeConverter<URI, String> {

    @Override
    @SneakyThrows
    public String convertToDatabaseColumn(URI uri) {
        return uri == null ? null : uri.toString();
    }

    @Override
    @SneakyThrows
    public URI convertToEntityAttribute(String uri) {
        return uri == null ? null : URI.create(uri);
    }

}
