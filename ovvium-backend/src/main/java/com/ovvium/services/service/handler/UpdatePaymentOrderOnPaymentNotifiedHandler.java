package com.ovvium.services.service.handler;

import com.ovvium.services.model.payment.PaymentNotifiedEvent;
import com.ovvium.services.repository.PaymentOrderAppCardRepository;
import com.ovvium.services.service.PaymentService;
import com.ovvium.services.service.event.EventHandler;
import com.ovvium.services.transfer.command.payment.PaymentNotificationCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UpdatePaymentOrderOnPaymentNotifiedHandler extends EventHandler<PaymentNotifiedEvent> {

    private final PaymentService paymentService;
    private final PaymentOrderAppCardRepository paymentOrderAppCardRepository;

    @Override
    public void handle(PaymentNotifiedEvent event) {
        var request = event.getRequest();
        var paymentOrderApp = paymentOrderAppCardRepository.getByPciTransactionId(UUID.fromString(request.getOrder()));
        paymentService.updatePaymentOnNotification(new PaymentNotificationCommand(
                paymentOrderApp,
                request.getAuthCode(),
                request.getResponse(),
                request.getErrorID(),
                request.getErrorDescription(),
                request.getAmount(),
                request.getCurrency()
        ));
    }

}
