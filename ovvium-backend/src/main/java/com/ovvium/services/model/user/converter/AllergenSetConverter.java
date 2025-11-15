package com.ovvium.services.model.user.converter;

import com.ovvium.services.model.user.Allergen;
import com.ovvium.services.util.jpa.converter.GenericEnumSetConverter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Set;

@Converter
public class AllergenSetConverter extends GenericEnumSetConverter<Allergen>
		implements AttributeConverter<Set<Allergen>, String> {

	public AllergenSetConverter() {
		super(Allergen.class);
	}
}
