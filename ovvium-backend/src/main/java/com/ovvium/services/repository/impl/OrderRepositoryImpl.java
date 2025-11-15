package com.ovvium.services.repository.impl;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.ovvium.services.model.bill.Order;
import com.ovvium.services.repository.OrderRepository;
import com.ovvium.services.util.jpa.core.JpaDefaultRepository;


@Repository
public class OrderRepositoryImpl extends JpaDefaultRepository<Order, UUID> implements OrderRepository {

}
