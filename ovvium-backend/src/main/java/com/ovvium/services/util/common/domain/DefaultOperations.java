package com.ovvium.services.util.common.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface DefaultOperations<T extends Identifiable<K>, K extends Serializable> {

    T save(T entity);

    Optional<T> get(K id);

    T getOrFail(K id);

    List<T> list();

    List<T> list(Request request);

    Page<T> page(PageRequest request);

    long count();

    long count(Request request);

    boolean remove(T entity);

    boolean remove(K id);
}
