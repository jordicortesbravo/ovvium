package com.ovvium.services.util.common.domain;


import com.ovvium.services.util.common.domain.adapters.PageRequestAdapter;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Collection;

import static java.util.Collections.emptySet;

@Data
@EqualsAndHashCode(callSuper = true)
@XmlJavaTypeAdapter(PageRequestAdapter.class)
public class PageRequest extends Request {

    private static final long serialVersionUID = -243546784133467681L;

    private final int pageNumber;
    private final int pageSize;

    public PageRequest(Request request, int pageNumber, int pageSize) {
        super(request.getFilters(), request.getOrders(), request.isDistinct());
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    public PageRequest filter(Filter filter) {
        return new PageRequest(super.filter(filter), pageNumber, pageSize);
    }

    public PageRequest filter(Filter.Condition condition, String field, Object value) {
        return new PageRequest(super.filter(condition, field, value), pageNumber, pageSize);
    }

    public PageRequest sort(Order order) {
        return new PageRequest(super.sort(order), pageNumber, pageSize);
    }

    public PageRequest sort(String field, Direction direction) {
        return new PageRequest(super.sort(field, direction), pageNumber, pageSize);
    }

    public PageRequest distinct(boolean isDistinct) {
        return new PageRequest(super.distinct(isDistinct), pageNumber, pageSize);
    }

    public static PageRequest of(Collection<Filter> filters, Pageable pageable) {
        Request rq = Request.of(filters, pageable.getSort());
        return rq.page(pageable.getPageNumber(), pageable.getPageSize());
    }

    public static PageRequest of(Pageable pageable) {
        Request rq = Request.of(emptySet(), pageable.getSort());
        return rq.page(pageable.getPageNumber(), pageable.getPageSize());
    }

}
