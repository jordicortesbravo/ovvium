package com.ovvium.services.service.application;


import com.ovvium.mother.model.PaymentOrderMother;
import com.ovvium.services.repository.PaymentOrderAppCardRepository;
import com.ovvium.services.repository.client.payment.PaymentClient;
import com.ovvium.services.service.EventPublisherService;
import com.ovvium.services.web.controller.bff.v1.transfer.request.webhooks.paycomet.PaycometWebhookRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;

import static com.ovvium.services.repository.client.payment.ws.dto.PaycometTransactionType.AUTHORIZATION;
import static com.ovvium.services.repository.client.payment.ws.dto.PaycometTransactionType.SPLIT_TRANSFER;
import static org.mockito.Mockito.*;

public class WebhooksApplicationServiceTest {

    private WebhooksApplicationService service;

    private PaymentOrderAppCardRepository paymentOrderAppCardRepository;
    private EventPublisherService eventPublisherService;
    private PaymentClient paymentClient;
    private Environment props;

    @BeforeEach
    void setUp() {
        paymentOrderAppCardRepository = mock(PaymentOrderAppCardRepository.class);
        eventPublisherService = mock(EventPublisherService.class);
        paymentClient = mock(PaymentClient.class);
        props = mock(Environment.class);
        when(props.getRequiredProperty("log.encryption.secret")).thenReturn("key");
        service = new WebhooksApplicationService(paymentOrderAppCardRepository, paymentClient, eventPublisherService, props);
    }

    @Test
    void given_not_implemented_notification_when_confirm_webhook_then_should_not_emit_event() {
        var request = new PaycometWebhookRequest();
        request.setTransactionType(SPLIT_TRANSFER.getValue());

        service.handlePaycometNotification(request);

        verifyNoInteractions(eventPublisherService);
    }


    @Test
    void given_not_pending_payment_order_when_confirm_webhook_then_should_not_emit_event() {
        var paymentOrder = PaymentOrderMother.anyPaymentOrderAppCard().confirmAsPaid();
        var request = new PaycometWebhookRequest();
        request.setTransactionType(AUTHORIZATION.getValue());
        request.setOrder(paymentOrder.getPciTransactionId().toString());
        when(paymentOrderAppCardRepository.getByPciTransactionId(paymentOrder.getPciTransactionId())).thenReturn(paymentOrder);
        when(paymentClient.verifyNotification(any())).thenReturn(true);

        service.handlePaycometNotification(request);

        verifyNoInteractions(eventPublisherService);
    }
}