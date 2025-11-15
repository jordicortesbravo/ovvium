package com.ovvium.services.model.payment;

import com.ovvium.services.util.ovvium.domain.event.AbstractOvviumEvent;
import com.ovvium.services.web.controller.bff.v1.transfer.request.webhooks.paycomet.PaycometWebhookRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public final class PaymentNotifiedEvent extends AbstractOvviumEvent {

	private final PaycometWebhookRequest request;

}
