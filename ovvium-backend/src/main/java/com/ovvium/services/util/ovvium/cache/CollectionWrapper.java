package com.ovvium.services.util.ovvium.cache;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Hazelcast helper class so a List can be serialized-deserialized using Kryo.
 * Hazelcast serializes each of the objects separately when it receives a Collection,
 * so this is a workaround so it uses a configured Serializer with Kryo.
 * <p>
 * FIXME We need to get rid of this...
 *
 * @param <T>
 */
@EqualsAndHashCode(of = "key")
@RequiredArgsConstructor
public class CollectionWrapper<T> implements Serializable {

	private final UUID key;
	private final Collection<T> collection;

	public Collection<T> unwrap() {
		return collection;
	}

	public List<T> toList() {
		return new ArrayList<>(unwrap());
	}

	public static <T> CollectionWrapper<T> of(UUID key, Collection<T> collection) {
		return new CollectionWrapper<>(key, collection);
	}

}
