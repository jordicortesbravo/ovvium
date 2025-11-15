package com.ovvium.services.util.common.domain;


import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.ovvium.services.util.common.domain.adapters.PageAdapter;

import java.util.Iterator;
import java.util.List;

@XmlJavaTypeAdapter(PageAdapter.class)
public interface Page<T> extends Iterable<T> {

    int getPageNumber();

    int getPageSize();

    int getTotalPages();

    int getNumberOfElements();

    long getTotalElements();

    boolean hasPreviousPage();

    boolean isFirstPage();

    boolean hasNextPage();

    boolean isLastPage();

    Iterator<T> iterator();

    List<T> getContent();

    boolean hasContent();
    
    <E> SimplePage<E> update(List<E> newContent);
}
