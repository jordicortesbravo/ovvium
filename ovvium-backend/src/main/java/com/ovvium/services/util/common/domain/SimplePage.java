package com.ovvium.services.util.common.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Data
public class SimplePage<T> implements Page<T>, Serializable {

    // FIXME This should be changed to 1
    public static final int FIRST_PAGE = 0;
    private static final long serialVersionUID = -2438394309553521828L;
    
    private final List<T> content;
    private final int pageNumber;
    private final int pageSize;
    private final long totalElements;

    public static <X> SimplePage<X> of(List<X> content) {
        return new SimplePage<X>(content, FIRST_PAGE, content.size(), content.size());
    }

    public <E> SimplePage<E> update(List<E> newContent) {
        return new SimplePage<E>(newContent, pageNumber, pageSize, totalElements);
    }

    public List<T> getContent() {
        return new ArrayList<T>(content);
    }

    @Override
    public int getTotalPages() {
        return pageSize == 0 ? 0 : (int) Math.ceil((double) totalElements / pageSize);
    }

    @Override
    public int getNumberOfElements() {
        return content.size();
    }

    @Override
    public boolean hasPreviousPage() {
        return FIRST_PAGE < pageNumber;
    }

    @Override
    public boolean isFirstPage() {
        return pageNumber == FIRST_PAGE;
    }

    @Override
    public boolean hasNextPage() {
        return pageNumber < getTotalPages() - 1;
    }

    @Override
    public boolean isLastPage() {
        return pageNumber == getTotalPages() - 1 || getTotalPages() == 0;
    }

    @Override
    public Iterator<T> iterator() {
        return content.iterator();
    }

    @Override
    public boolean hasContent() {
        return !content.isEmpty();
    }

}
