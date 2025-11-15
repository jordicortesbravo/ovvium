package com.ovvium.services.util.common.domain;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ovvium.services.util.common.Utils;

@Data
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class Request implements Serializable {

    private static final long serialVersionUID = -6918446984427231769L;
    
    private final Set<Filter> filters;
    private final List<Order> orders;
    private final boolean distinct;

    public Request() {
        this(new HashSet<Filter>(), new ArrayList<Order>(), false);
    }

    public Set<Filter> getFilters() {
        return new HashSet<Filter>(filters);
    }

    public List<Order> getOrders() {
        return new ArrayList<Order>(orders);
    }

    public Request filter(Filter filter) {
        return new Request(Utils.with(filters, filter), orders, distinct);
    }

    public Request filter(Filter.Condition condition, String field, Object value) {
        return filter(new Filter(field, value, condition));
    }

    public Request sort(Order order) {
        return new Request(filters, Utils.with(orders, order), distinct);
    }

    public Request sort(String field, Direction direction) {
        return sort(new Order(direction, field));
    }

    public Request distinct(boolean isDistinct) {
        return new Request(filters, orders, isDistinct);
    }

    public PageRequest page(int page, int size) {
        return new PageRequest(this, page, size);
    }

    public static Request of(Iterable<Filter> filters, Iterable<Order> orders) {
        Request rq = new Request();
        if (filters != null) {
            for (val filter : filters) {
                rq = rq.filter(filter);
            }
        }
        if (orders != null) {
            for (val order : orders) {
                rq = rq.sort(order);
            }
        }
        return rq;
    }

}
