package com.ovvium.services.web.controller.bff.v1.transfer.response.payment;

import com.ovvium.services.model.payment.Invoice;
import com.ovvium.services.model.payment.PaymentOrder;
import com.ovvium.services.model.payment.PaymentType;
import com.ovvium.services.web.controller.bff.v1.transfer.response.common.ResourceIdResponse;
import lombok.Getter;

import java.util.UUID;

@Getter
public abstract class PaymentOrderResponse extends ResourceIdResponse {

    private final String type;
    private final UUID invoiceId;
    private final PaymentType paymentType;

    protected PaymentOrderResponse(PaymentOrder paymentOrder, String type) {
        super(paymentOrder);
        this.type = type;
        this.paymentType = paymentOrder.getPaymentType();
        this.invoiceId = paymentOrder.getInvoice().map(Invoice::getId).orElse(null);
    }
}
