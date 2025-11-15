package com.ovvium.services.model.payment;

import com.ovvium.services.util.ovvium.domain.event.AbstractOvviumEvent;
import lombok.Data;

import java.util.UUID;

@Data
public final class PaymentExecutedEvent extends AbstractOvviumEvent {

	private final UUID paymentOrderId;

}
