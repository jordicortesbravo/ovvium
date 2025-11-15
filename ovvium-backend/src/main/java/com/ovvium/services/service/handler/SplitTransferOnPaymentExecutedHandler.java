package com.ovvium.services.service.handler;

import com.ovvium.services.model.payment.PaymentExecutedEvent;
import com.ovvium.services.service.PaymentService;
import com.ovvium.services.service.event.EventHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SplitTransferOnPaymentExecutedHandler extends EventHandler<PaymentExecutedEvent> {

	private final PaymentService paymentService;

	@Override
	public void handle(PaymentExecutedEvent event) {
		paymentService.executeSplitTransfer(event.getPaymentOrderId());
	}

}
