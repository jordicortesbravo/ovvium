package com.ovvium.services.util.jpa.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.ovvium.services.util.common.type.Email;

@Converter(autoApply = true)
public class EmailConverter implements AttributeConverter<Email, String> {

    @Override
    public String convertToDatabaseColumn(Email email) {
        return email == null ? null : email.toString();
    }

    @Override
    public Email convertToEntityAttribute(String email) {
        return email == null ? null : new Email(email);
    }

}
