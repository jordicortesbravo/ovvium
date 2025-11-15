package com.ovvium.services.repository;


import java.util.UUID;

import com.ovvium.services.model.bill.Order;
import com.ovvium.services.util.jpa.core.DefaultRepository;

public interface OrderRepository extends DefaultRepository<Order, UUID> {

}
