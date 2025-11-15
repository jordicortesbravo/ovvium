package com.ovvium.services.util.ovvium.base;

import com.google.common.collect.Iterables;
import com.ovvium.services.util.util.reflection.ReflectionUtils;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import static org.apache.commons.lang.StringUtils.isBlank;

@UtilityClass
public class Preconditions {

	public static <T> T checkNotNull(T value, String reason) {
		return checkNotNull(value, reason, IllegalArgumentException.class);
	}

	@SneakyThrows
	public static <T> T checkNotNull(T value, String reason, Class<? extends Exception> exc) {
		if (value == null) {
			throw ReflectionUtils.create(exc, reason);
		}
		return value;
	}

	public static String checkNotBlank(String value, String reason) {
		return check(value, !isBlank(value), reason);
	}
	
	@SneakyThrows
	public static String checkNotBlank(String value, String reason, Class<? extends Exception> exc) {
		if (isBlank(value)) {
			throw ReflectionUtils.create(exc, reason);
		}
		return value;
	}

	public static <C extends Collection<T>,T> C checkMinSize(C collection,  int minSize, String reason) {
		return check(collection, checkNotNull(collection, reason).size() >= minSize, reason);
	}

	public static void checkAllNotBlank(String reason, String... values) {
		Stream.of(values).forEach(v -> checkNotBlank(v, reason));
	}

	public static String checkMaxCharacters(String value, int maxSize, String reason) {
		return check(value, value.length() <= maxSize, reason);
	}

	public static <T extends Iterable<?>> T checkNotEmpty(T iterable, String reason) {
		return check(iterable, iterable !=null && !Iterables.isEmpty(iterable), reason);
	}

	public static double checkRange(double value, double min, double max, String reason) {
		return check(value, value >= min && value <= max, reason);
	}

	public static <T> T check(T value, boolean predicate, String message) {
		check(predicate, message);
		return value;
	}

	public static <T> T checkIsPresent(Optional<T> opt, String message) {
		check(opt.isPresent(), message);
		return opt.get();
	}

	/**
	 * Generic Precondition, if predicate is false, throws  IllegalArgumentException.
	 */
	public static void check(boolean predicate, String message) {
		check(predicate, new IllegalArgumentException(message));
	}

	/**
	 * Generic Precondition, if predicate is false, throws supplied exception.
	 */
	public static <X extends Throwable> void check(boolean predicate, X exceptionThrown) throws X {
		if (!predicate) {
			throw exceptionThrown;
		}
	}

}
