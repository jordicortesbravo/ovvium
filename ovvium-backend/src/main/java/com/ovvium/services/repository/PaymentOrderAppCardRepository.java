package com.ovvium.services.repository;

import com.ovvium.services.model.payment.PaymentOrderApp;
import com.ovvium.services.util.jpa.core.DefaultRepository;

import java.util.UUID;

public interface PaymentOrderAppCardRepository extends DefaultRepository<PaymentOrderApp, UUID> {

    PaymentOrderApp getByPciTransactionId(UUID pciTransactionId);

}
