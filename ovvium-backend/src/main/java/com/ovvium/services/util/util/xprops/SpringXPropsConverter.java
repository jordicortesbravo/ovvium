package com.ovvium.services.util.util.xprops;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;

import java.util.Arrays;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Converts Spring´s Environment variable to XProps, so we can use Spring Environment functionalities with XProps.
 */
public class SpringXPropsConverter {

	public static XProps convert(ConfigurableEnvironment env) {
		final SortedMap<String, String> sortedMap = new TreeMap<>();
		env.getPropertySources().stream() //
				.filter(propertySource -> !propertySource.getName().contains("system")) //
				.filter(propertySource -> propertySource instanceof EnumerablePropertySource) //
				.map(ps -> ((EnumerablePropertySource<?>) ps).getPropertyNames()) //
				.flatMap(Arrays::stream) //
				.forEach(name -> addPropertyToMap(env, sortedMap, name));
		return new XProps().add(sortedMap);
	}

	private static String addPropertyToMap(ConfigurableEnvironment env, SortedMap<String, String> sortedMap,
			String name) {
		return sortedMap.computeIfAbsent(name, (it) -> Optional.ofNullable(env.getProperty(it)) //
				.map(Object::toString) //
				.orElse(null));
	}

}
