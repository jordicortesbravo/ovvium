package com.ovvium.services.util.util.container;

import lombok.experimental.UtilityClass;
import lombok.val;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class Maps {

	public static <K, V> Map<K, V> emptyMap() {
		return new EmptyMap<>();
	}

	@SafeVarargs
	@Deprecated
	public static <K, V> Map<K, V> map(Pair<? extends K, ? extends V>... entries) {
		return map(new HashMap<K, V>(), entries);
	}

	@SafeVarargs
	@Deprecated
	public static <K, V> Map<K, V> map(Map<K, V> map, Pair<? extends K, ? extends V>... entries) {
		for (val entry : entries) {
			map.put(entry.getFirst(), entry.getSecond());
		}
		return map;
	}

	public static <K, V> FluentMap<K, V> map() {
		return new FluentMap<K, V>(null, null);
	}

	public static <K, V> FluentMap<K, V> map(Class<K> k, Class<V> v) {
		return new FluentMap<K, V>(k, v);
	}

	public static FluentMap<String, String> mapSS() {
		return map(String.class, String.class);
	}

	public static FluentMap<String, Object> mapSO() {
		return map(String.class, Object.class);
	}

	public static FluentMap<Object, Object> mapOO() {
		return map(Object.class, Object.class);
	}
}
