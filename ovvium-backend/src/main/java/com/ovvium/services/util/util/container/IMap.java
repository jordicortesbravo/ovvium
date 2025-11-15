package com.ovvium.services.util.util.container;

import java.util.*;

import lombok.val;

/**
 * @deprecated use Utils.map() instead
 */
@Deprecated
public class IMap<K, V> {

    private Map<K, V> delegate = new HashMap<K, V>();

    public static <K, V> IMap<K, V> of(K key, V value) {
        return new IMap<K, V>().put(key, value);
    }

    public IMap<K, V> put(K key, V value) {
        delegate.put(key, value);
        return this;
    }

    public Map<K, V> build() {
        val built = Collections.unmodifiableMap(delegate);
        delegate = null;
        return built;
    }

}
