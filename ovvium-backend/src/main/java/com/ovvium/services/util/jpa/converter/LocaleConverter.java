package com.ovvium.services.util.jpa.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Locale;

@Converter(autoApply = true)
public class LocaleConverter implements AttributeConverter<Locale, String> {

    @Override
    public String convertToDatabaseColumn(Locale locale) {
        return locale == null ? null : locale.toLanguageTag();
    }

    @Override
    public Locale convertToEntityAttribute(String localeTag) {
        return localeTag == null ? null : Locale.forLanguageTag(localeTag);
    }

}
