package com.ovvium.services.util.util.container;

import java.util.*;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FluentMap<K, V> extends LinkedHashMap<K, V> {

    private static final long serialVersionUID = 6244993200248921576L;

    private final Class<K> keyClass;
    private final Class<V> valueClass;

    public FluentMap<K, V> with(K key, V value) {
        super.put(key, value);
        return this;
    }

    public Map<K, V> unmodifiable() {
        return Collections.unmodifiableMap(this);
    }

    public Map<K, V> synched() {
        return Collections.synchronizedMap(this);
    }

    public Map<K, V> safe() {
        if (keyClass == null || valueClass == null) {
            throw new IllegalStateException("Can't perform type checks if key or value types are not specified");
        }
        return Collections.checkedMap(this, keyClass, valueClass);
    }

}
