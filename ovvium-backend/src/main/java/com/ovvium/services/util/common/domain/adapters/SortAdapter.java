package com.ovvium.services.util.common.domain.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.ovvium.services.util.common.domain.Order;
import com.ovvium.services.util.common.domain.Sort;
import com.ovvium.services.util.common.domain.transfers.OrderTransfer;
import com.ovvium.services.util.common.domain.transfers.SortTransfer;

import java.util.LinkedList;
import java.util.List;


public class SortAdapter extends XmlAdapter<SortTransfer, Sort>{

    private OrderAdapter orderAdapter = new OrderAdapter();
    
    @Override
    public Sort unmarshal(SortTransfer sortTransfer) {
        if(sortTransfer == null || sortTransfer.getOrders() == null || sortTransfer.getOrders().isEmpty()) {
            return null;
        }
        
        List<Order> orders = new LinkedList<Order>();
        for(OrderTransfer orderTransfer : sortTransfer.getOrders())  {
            orders.add(orderAdapter.unmarshal(orderTransfer));
        }
        return new Sort(orders);
    }

    @Override
    public SortTransfer marshal(Sort sort) {
        
        if(sort == null) {
            return null;
        }
       
        List<OrderTransfer> orderTransfers = new LinkedList<OrderTransfer>();
        for(Order order: sort) {
            orderTransfers.add(orderAdapter.marshal(order));
        }
        
        return new SortTransfer(orderTransfers);
    }
}
