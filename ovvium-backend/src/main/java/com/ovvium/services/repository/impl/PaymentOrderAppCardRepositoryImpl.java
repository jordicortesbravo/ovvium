package com.ovvium.services.repository.impl;

import com.ovvium.services.model.payment.PaymentOrderApp;
import com.ovvium.services.model.payment.QPaymentOrderApp;
import com.ovvium.services.repository.PaymentOrderAppCardRepository;
import com.ovvium.services.util.jpa.core.JpaDefaultRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class PaymentOrderAppCardRepositoryImpl extends JpaDefaultRepository<PaymentOrderApp, UUID> implements PaymentOrderAppCardRepository {

    private static final QPaymentOrderApp qPaymentOrderApp = QPaymentOrderApp.paymentOrderApp;

    @Override
    public PaymentOrderApp getByPciTransactionId(UUID pciTransactionId) {
        return getOrFail(qPaymentOrderApp.pciTransactionId.eq(pciTransactionId));
    }
}
