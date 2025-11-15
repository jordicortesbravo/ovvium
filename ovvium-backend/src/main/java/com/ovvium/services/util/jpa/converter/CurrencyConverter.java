package com.ovvium.services.util.jpa.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Currency;

@Converter(autoApply = true)
public class CurrencyConverter implements AttributeConverter<Currency, String> {

	@Override
	public String convertToDatabaseColumn(Currency currency) {
		return currency == null ? null : currency.getCurrencyCode();
	}

	@Override
	public Currency convertToEntityAttribute(String currencyCode) {
		return currencyCode == null ?  null : Currency.getInstance(currencyCode);
	}
}