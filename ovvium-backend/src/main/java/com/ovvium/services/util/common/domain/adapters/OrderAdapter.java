package com.ovvium.services.util.common.domain.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.ovvium.services.util.common.domain.Direction;
import com.ovvium.services.util.common.domain.Order;
import com.ovvium.services.util.common.domain.transfers.OrderTransfer;

public class OrderAdapter extends XmlAdapter<OrderTransfer, Order>{

    @Override
    public Order unmarshal(OrderTransfer orderTransfer) {
        if(orderTransfer == null || orderTransfer.getProperty() == null || orderTransfer.getProperty().isEmpty()) {
            return null;
        }
        
        return new Order(Direction.valueOf(orderTransfer.getDirection()), orderTransfer.getProperty());
    }

    @Override
    public OrderTransfer marshal(Order order) {
        
        if(order == null) {
            return null;
        }
        return new OrderTransfer(order.getDirection().toString(), order.getProperty());
    }
}
