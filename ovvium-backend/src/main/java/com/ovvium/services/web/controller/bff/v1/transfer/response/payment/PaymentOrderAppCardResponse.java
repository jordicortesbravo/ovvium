package com.ovvium.services.web.controller.bff.v1.transfer.response.payment;

import com.ovvium.services.model.payment.PaymentOrderApp;
import com.ovvium.services.model.payment.PaymentOrderStatus;
import lombok.Getter;

import java.net.URI;

@Getter
public final class PaymentOrderAppCardResponse extends PaymentOrderResponse {

    private final PaymentOrderStatus status;
    private final URI challengeUrl;

    public PaymentOrderAppCardResponse(PaymentOrderApp paymentOrderApp, URI challengeUrl) {
        super(paymentOrderApp, "APP");
        this.challengeUrl = challengeUrl;
        this.status = paymentOrderApp.getStatus();
    }

}
