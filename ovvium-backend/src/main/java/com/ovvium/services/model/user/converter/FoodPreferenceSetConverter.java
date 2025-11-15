package com.ovvium.services.model.user.converter;

import com.ovvium.services.model.user.FoodPreference;
import com.ovvium.services.util.jpa.converter.GenericEnumSetConverter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Set;

@Converter
public class FoodPreferenceSetConverter extends GenericEnumSetConverter<FoodPreference>
		implements AttributeConverter<Set<FoodPreference>, String> {

	public FoodPreferenceSetConverter() {
		super(FoodPreference.class);
	}
}
