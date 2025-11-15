package com.ovvium.services.util.common.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.hibernate.Hibernate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Identifiables {

    /**
     * Creates a map containing passed {@link Identifiable} objects, using id property as key.
     */
    public static <T extends Identifiable<K>, K> Map<K, T> asMap(Collection<T> identifiables) {
        Map<K, T> map = new HashMap<K, T>(identifiables.size());
        for (T object : identifiables) {
            map.put(object.getId(), object);
        }
        return map;
    }

    /**
     * Returns a list containing ids of passed {@link Identifiable} objects.
     */
    public static <T extends Identifiable<K>, K> List<K> getIds(Collection<T> identifiables) {
        List<K> ids = new ArrayList<K>(identifiables.size());
        for (Identifiable<K> entity : identifiables) {
            ids.add(entity.getId());
        }
        return ids;
    }

    /**
     * Sorts a list of {@link Identifiable} objects by id, in same order as passed id list. If an object with
     */
    public static <T extends Identifiable<K>, K> void sort(List<T> identifiables, List<K> ids) {
        Map<K, T> entityMap = asMap(identifiables);
        identifiables.clear();
        for (K id : ids) {
            T entity = entityMap.get(id);
            if (entity != null) {
                identifiables.add(entity);
            }
        }
    }

    public static <T extends Enum<T> & Identifiable<S>, S> T get(Class<T> type, S id) {
        for (T t : type.getEnumConstants()) {
            if (nullSafeEquals(t.getId(), id)) {
                return t;
            }
        }
        return null;
    }

    public static <T extends Enum<T> & Identifiable<S>, S extends Comparable<? super S>> List<T> getLower(T en) {
        List<T> list = new ArrayList<T>();
        for (T t : en.getDeclaringClass().getEnumConstants()) {
            if (t.getId().compareTo(en.getId()) < 0) {
                list.add(t);
            }
        }
        return list;
    }

    public static <T extends Enum<T> & Identifiable<S>, S extends Comparable<? super S>> List<T> getHigher(T en) {
        List<T> list = new ArrayList<T>();
        for (T t : en.getDeclaringClass().getEnumConstants()) {
            if (t.getId().compareTo(en.getId()) > 0) {
                list.add(t);
            }
        }
        return list;
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static <T extends Identifiable<K>, K> Class<K> getIdClass(Class<T> entityClass) {
        return (Class<K>) entityClass.getMethod(Identifiable.ID_METHOD).getReturnType();
    }

    private static boolean nullSafeEquals(Object a, Object b) {
        if (a == null) {
            return b == null;
        }
        return a.equals(b);
    }

    public static boolean equals(Identifiable<?> a, Object b) {
        if (a == b) {
            return true;
        }
        if (b == null || !Hibernate.getClass(b).equals(Hibernate.getClass(a)) || a.getId() == null) {
            return false;
        }
        return a.getId().equals(((Identifiable<?>) b).getId());
    }

    public static int hashCode(Identifiable<?> object) {
        val id = object.getId();
        if (id != null) {
            return Objects.hash(object.getId());
        }
        return System.identityHashCode(object);
    }

}
