package com.ovvium.services.model.common;

import com.google.common.collect.Sets;
import lombok.experimental.UtilityClass;

import java.util.Locale;
import java.util.Set;

import static java.util.Locale.*;

@UtilityClass
public class LocaleConstants {

	public static final Locale SPANISH = new Locale("es", "ES");
	public static final Locale CATALAN = new Locale("ca", "ES");

	public static final Set<Locale> ALLOWED_LOCALES = Sets.newHashSet(SPANISH, CATALAN, UK, SIMPLIFIED_CHINESE, FRANCE, GERMANY);

	public static final Locale DEFAULT_LOCALE = SPANISH;

}
