package com.ovvium.services.util.jpa.converter;

import java.net.URL;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import lombok.SneakyThrows;

@Converter(autoApply = true)
public class URLConverter implements AttributeConverter<URL, String> {

    @Override
    @SneakyThrows
    public String convertToDatabaseColumn(URL url) {
        return url == null ? null : url.toString();
    }

    @Override
    @SneakyThrows
    public URL convertToEntityAttribute(String url) {
        return url == null ? null : new URL(url);
    }

}
