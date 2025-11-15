package com.ovvium.services.util.ovvium.base;

import com.google.common.base.Joiner;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import javax.validation.MessageInterpolator;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Locale;

import static java.lang.String.format;

@Slf4j
@UtilityClass
public final class Validations {

	private static class LocaleSpecificMessageInterpolator implements MessageInterpolator {
		private final MessageInterpolator defaultInterpolator;
		private final Locale defaultLocale;

		private LocaleSpecificMessageInterpolator(MessageInterpolator interpolator, Locale locale) {
			this.defaultLocale = locale;
			this.defaultInterpolator = interpolator;
		}

		public String interpolate(String message, Context context) {
			return defaultInterpolator.interpolate(message, context, this.defaultLocale);
		}

		public String interpolate(String message, Context context, Locale locale) {
			return defaultInterpolator.interpolate(message, context, locale);
		}
	}

	private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

	private static final Validator validator;

	static {
		ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();

		MessageInterpolator interpolator = new LocaleSpecificMessageInterpolator(
				validatorFactory.getMessageInterpolator(), DEFAULT_LOCALE);

		validator = validatorFactory.usingContext().messageInterpolator(interpolator).getValidator();
	}

	public static <T> void validate(T object) {

		validator.validate(object).stream() //
				.map(violation -> format("%s %s", violation.getPropertyPath(), violation.getMessage())) //
				.reduce((s1, s2) -> Joiner.on(", ").join(s1, s2)) //
				.ifPresent((error) -> {
					log.warn("Validation with errors: {}", error);
					throw new IllegalArgumentException(error);
				});

	}

}
