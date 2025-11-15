package com.ovvium.services.web.controller.bff.v1.transfer.request.payment;

import com.ovvium.services.model.payment.PaymentType;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.UUID;

@Data
@Accessors(chain = true)
public class PaymentInvoiceRequest {

	private UUID invoiceId;
	private PaymentType type;

}
