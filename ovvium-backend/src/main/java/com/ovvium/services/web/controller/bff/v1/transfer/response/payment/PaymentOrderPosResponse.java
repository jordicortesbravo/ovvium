package com.ovvium.services.web.controller.bff.v1.transfer.response.payment;

import com.ovvium.services.model.payment.PaymentOrder;
import lombok.Getter;

@Getter
public final class PaymentOrderPosResponse extends PaymentOrderResponse {

    public PaymentOrderPosResponse(PaymentOrder paymentOrder) {
        super(paymentOrder, "POINT_OF_SALE");
    }

}
