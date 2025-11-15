package com.ovvium.services.util.common.domain.transfers;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import com.ovvium.services.util.common.domain.Filter;
import com.ovvium.services.util.common.domain.Order;

@Data
public class PageRequestTransfer implements Serializable{

    private static final long serialVersionUID = -3431810940115238854L;
    
    private Set<Filter> filters;
    private List<Order> orders;
    private boolean distinct;
    private int pageNumber;
    private int pageSize;
}
