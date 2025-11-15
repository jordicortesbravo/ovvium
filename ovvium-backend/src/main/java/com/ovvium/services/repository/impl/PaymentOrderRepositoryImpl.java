package com.ovvium.services.repository.impl;

import com.ovvium.services.model.payment.PaymentOrder;
import com.ovvium.services.repository.PaymentOrderRepository;
import com.ovvium.services.util.jpa.core.JpaDefaultRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class PaymentOrderRepositoryImpl extends JpaDefaultRepository<PaymentOrder, UUID> implements PaymentOrderRepository {

}
