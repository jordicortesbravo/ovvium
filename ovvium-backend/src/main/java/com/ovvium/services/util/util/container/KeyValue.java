package com.ovvium.services.util.util.container;

import java.util.Comparator;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "key")
public class KeyValue<K, V> {

    public static class KeyValueComparator<K2 extends Comparable<K2>> implements Comparator<KeyValue<K2, ?>> {

        @Override
        public int compare(KeyValue<K2, ?> o1, KeyValue<K2, ?> o2) {
            return o1.getKey().compareTo(o2.getKey());
        }

    }

    public static <X extends Comparable<X>> KeyValueComparator<X> getComparator() {
        return new KeyValueComparator<X>();
    }

    private K key;
    private V value;

    public static <K, V> KeyValue<K, V> of(K key, V value) {
        return new KeyValue<K, V>(key, value);
    }

}
