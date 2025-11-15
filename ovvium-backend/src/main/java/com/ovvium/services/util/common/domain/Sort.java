package com.ovvium.services.util.common.domain;

import com.ovvium.services.util.common.domain.adapters.SortAdapter;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


@XmlJavaTypeAdapter(SortAdapter.class)
public class Sort implements Iterable<Order>, Serializable {

    private static final long serialVersionUID = 8266656350830321205L;

    private final List<Order> orders;

    public Sort(Order... orders) {

        this(Arrays.asList(orders));
    }

    public Sort(List<Order> orders) {

        if (null == orders || orders.isEmpty()) {
            throw new IllegalArgumentException("You have to provide at least one sort property to sort by!");
        }

        this.orders = orders;
    }

    public Sort(String... properties) {

        this(Direction.DEFAULT_DIRECTION, properties);
    }

    public Sort(Direction direction, String... properties) {

        this(direction, properties == null ? new ArrayList<String>() : Arrays.asList(properties));
    }

    public Sort(Direction direction, List<String> properties) {

        if (properties == null || properties.isEmpty()) {
            throw new IllegalArgumentException("You have to provide at least one property to sort by!");
        }

        this.orders = new ArrayList<Order>(properties.size());

        for (String property : properties) {
            this.orders.add(new Order(direction, property));
        }
    }

    public Order getOrderFor(String property) {
        for (Order order : this) {
            if (order.getProperty().equals(property)) {
                return order;
            }
        }

        return null;
    }

    public static Sort desc(String...  properties) {
        return new Sort(Direction.DESC, properties);
    }

    public static Sort asc(String...  properties) {
        return new Sort(Direction.ASC, properties);
    }

    public Iterator<Order> iterator() {
        return this.orders.iterator();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Sort)) {
            return false;
        }

        Sort that = (Sort) obj;

        return this.orders.equals(that.orders);
    }

    // TODO: ...
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + orders.hashCode();
        return result;
    }

    @Override
    public String toString() {
        if (orders == null || orders.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        Iterator<?> it = orders.iterator();
        while (it.hasNext()) {
            sb.append(it.next());
            if (it.hasNext()) {
                sb.append(",");
            }
        }

        return sb.toString();
    }
}
