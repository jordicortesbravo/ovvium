package com.ovvium.services.util.util.basic;

import java.util.*;

import lombok.*;

// TODO: Trerure dependències lletges
import org.apache.commons.collections.iterators.ArrayIterator;
import org.springframework.util.CollectionUtils;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class Traverser<E> implements Iterator<E>, Iterable<E>, Enumeration<E> {

    public static <T> Traverser<T> of(Iterator<T> iterator) {
        if (iterator == null) {
            iterator = Collections.emptyIterator();
        }
        return new Traverser<T>(iterator);
    }

    public static <T> Traverser<T> of(Iterable<T> iterable) {
        return of(iterable == null ? null : iterable.iterator());
    }

    public static <T> Traverser<T> of(Enumeration<T> enumeration) {
        return of(enumeration == null ? null : CollectionUtils.toIterator(enumeration));
    }

    @SuppressWarnings("unchecked")
    public static <T> Traverser<T> of(T... elements) {
        return of(elements == null ? null : new ArrayIterator(elements));
    }

    private final Iterator<E> iterator;

    @Getter
    private E current;

    @Getter
    private int position = -1;

    @Override
    public Iterator<E> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public E next() {
        position++;
        current = iterator.next();
        return current;
    }

    @Override
    public void remove() {
        iterator.remove();
        position--;
        current = null;
    }

    @Override
    public boolean hasMoreElements() {
        return hasNext();
    }

    @Override
    public E nextElement() {
        return next();
    }

    public E next(E defaultValue) {
        return hasNext() ? next() : defaultValue;
    }

    public Traverser<E> advance() {
        next();
        return this;
    }

    public boolean isFirst() {
        if (position == -1) {
            throw new IllegalStateException("Traverser.next() wasn't called before Traverser.isFirst()");
        }
        return position == 0;
    }

    public boolean isLast() {
        return !hasNext();
    }

    public boolean mod(int divisor, int remainder) {
        return position % divisor == remainder;
    }

    public boolean isEven() {
        return mod(2, 0);
    }

    public boolean isOdd() {
        return mod(2, 1);
    }

}
