package com.ovvium.services.util.common.domain;

import lombok.Data;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.ovvium.services.util.common.domain.adapters.OrderAdapter;

import java.io.Serializable;

@Data
@XmlJavaTypeAdapter(OrderAdapter.class)
public class Order implements Serializable {

    private static final long serialVersionUID = 7701046970839982877L;

    private final Direction direction;
    private final String property;

    public Order(String property) {
        this(Direction.DEFAULT_DIRECTION, property);
    }

    public Order(Direction direction, String property) {

        this.direction = direction == null ? Direction.DEFAULT_DIRECTION : direction;

        if (property == null || "".equals(property.trim())) {
            throw new IllegalArgumentException("Property must not null or empty!");
        }

        this.property = property;
    }

    public boolean isAscending() {
        return direction.equals(Direction.ASC);
    }

    // public Order with(Direction order) {
    // return new Order(order, this.property);
    // }
    //
    // public Sort withProperties(String... properties) {
    // return new Sort(this.direction, properties);
    // }

    @Override
    public String toString() {
        return property + " " + direction;
    }
}
