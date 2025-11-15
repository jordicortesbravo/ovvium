package com.ovvium.services.repository;

import com.ovvium.services.model.payment.PaymentOrder;
import com.ovvium.services.util.jpa.core.DefaultRepository;

import java.util.UUID;

public interface PaymentOrderRepository extends DefaultRepository<PaymentOrder, UUID> {

}
