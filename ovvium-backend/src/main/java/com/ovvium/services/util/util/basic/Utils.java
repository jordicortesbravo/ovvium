package com.ovvium.services.util.util.basic;

import com.ovvium.services.util.util.container.Pair;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.val;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

@UtilityClass
public class Utils {

    @SafeVarargs
    public static <T> T firstNonNull(T... elements) {
        for (T el : elements) {
            if (el != null) {
                return el;
            }
        }
        return null;
    }

    @SafeVarargs
    public static <G, C extends Collection<G>> C firstNonEmpty(C... elements) {
        for (C el : elements) {
            if (el != null && !el.isEmpty()) {
                return el;
            }
        }
        return null;
    }

    public static <T> T first(Iterable<T> iterable) {
        if (iterable != null) {
            Iterator<T> it = iterable.iterator();
            if (it.hasNext()) {
                return it.next();
            }
        }
        return null;
    }

    public static <T> T first(T[] array) {
        return (array != null && array.length > 0) ? array[0] : null;
    }

    public static String first(String... values) {
        for (val value : values) {
            if (value != null && !value.trim().isEmpty()) {
                return value;
            }
        }
        return null;
    }
    
    public static <T> T last(List<T> list) {
        if (list != null) {
            val it = list.listIterator(list.size());
            if (it.hasPrevious()) {
                return it.previous();
            }
        }
        return null;
    }

    public static <T> T[] array(T... collection) {
        return collection;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] array(Collection<T> collection) {
        Object[] objects = collection.toArray();
        return (T[]) objects;
    }

    public static <K, V> Map<V, K> invert(Map<K, V> map) {
        Map<V, K> inverse = new HashMap<V, K>();
        for (Entry<K, V> entry : map.entrySet()) {
            inverse.put(entry.getValue(), entry.getKey());
        }
        return inverse;
    }

    public static <T> Set<T> set(T... array) {
        return array == null ? new HashSet<T>() : new HashSet<T>(Arrays.asList(array));
    }

    public static <T> Set<T> set(Collection<? extends Collection<? extends T>> collections) {
        val set = new HashSet<T>();
        for (val col : collections) {
            set.addAll(col);
        }
        return set;
    }

    public static <A, B> Pair<A, B> pair(A a, B b) {
        return new Pair<A, B>(a, b);
    }

    @SafeVarargs
    public static Properties props(Pair<String, String>... properties) {
        val props = new Properties();
        for (val p : properties) {
            props.setProperty(p.getFirst(), p.getSecond());
        }
        return props;
    }

    @SafeVarargs
    public static <T extends Collection<E>, E, S extends E> T collection(T collection, S... elements) {
        for (val e : elements) {
            collection.add(e);
        }
        return collection;
    }

    // TODO: El·liminar quan s'empri Objects.equals a Java 7
    public static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

    public static String makeString(Iterable<?> elements, String pre, String sep, String post) {
        val sb = new StringBuilder(pre);
        val tr = Traverser.of(elements);
        for (val el : tr) {
            sb.append(tr.isFirst() ? "" : sep).append(el == null ? "" : el);
        }
        return sb.append(post).toString();
    }

    public static final <T> Set<T> with(Set<T> set, T element) {
        val x = new HashSet<T>(set);
        x.add(element);
        return x;
    }

    public static final <T> List<T> with(List<T> list, T element) {
        val x = new ArrayList<T>(list);
        x.add(element);
        return x;
    }

    public static final <T> boolean in(T el, Iterable<T> iterable) {
        val it = iterable.iterator();
        while (it.hasNext()) {
            if (equals(el, it.next())) {
                return true;
            }
        }
        return false;
    }

    public static final <T> boolean in(T el, T... els) {
        for (T current : els) {
            if (equals(el, current)) {
                return true;
            }
        }
        return false;
    }

    public static <T> List<T> subList(List<T> list, int from, int to) {
        val size = list.size();
        return list.subList(Math.min(from, size), Math.min(to, size));
    }

    @SneakyThrows
    public static void close(Closeable... resources) {
        Throwable ex = null;
        for (val r : resources) {
            try {
                r.close();
            } catch (Throwable e) {
                ex = e;
            }
        }
        if (ex != null) {
            throw ex;
        }
    }
}
