package com.ovvium.services.repository.client.payment.dto;

import com.ovvium.services.model.payment.PaymentOrderApp;

public record VerifyNotificationRequest(
        PaymentOrderApp paymentOrder,
        String hash,
        Integer transactionType,
        Integer amount,
        String currency,
        String bankDateTime,
        String response
) {

}
