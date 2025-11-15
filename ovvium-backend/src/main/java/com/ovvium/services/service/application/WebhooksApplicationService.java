package com.ovvium.services.service.application;

import com.ovvium.services.model.payment.PaymentNotifiedEvent;
import com.ovvium.services.repository.PaymentOrderAppCardRepository;
import com.ovvium.services.repository.client.payment.PaymentClient;
import com.ovvium.services.repository.client.payment.dto.VerifyNotificationRequest;
import com.ovvium.services.service.EventPublisherService;
import com.ovvium.services.util.ovvium.encryption.EncryptionUtils;
import com.ovvium.services.web.controller.bff.v1.transfer.request.webhooks.paycomet.PaycometWebhookRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.ovvium.services.repository.client.payment.ws.dto.PaycometTransactionType.AUTHORIZATION;

@Slf4j
@Service
@Transactional
public class WebhooksApplicationService {

    private final PaymentOrderAppCardRepository paymentOrderAppCardRepository;
    private final PaymentClient paymentClient;
    private final EventPublisherService eventPublisherService;
    private final String encryptionSecret;

    public WebhooksApplicationService(PaymentOrderAppCardRepository paymentOrderAppCardRepository, PaymentClient paymentClient, EventPublisherService eventPublisherService, Environment props) {
        this.paymentOrderAppCardRepository = paymentOrderAppCardRepository;
        this.paymentClient = paymentClient;
        this.eventPublisherService = eventPublisherService;
        this.encryptionSecret = props.getRequiredProperty("log.encryption.secret");
    }

    public void handlePaycometNotification(PaycometWebhookRequest request) {
        log.info("Received Webhook notification : {}", EncryptionUtils.encryptAES(request.toString(), encryptionSecret));
        if (request.getTransactionType() != AUTHORIZATION.getValue()) {
            log.debug("Request was not a Payment Authorization Notification, TransactionType: {}", request.getTransactionType());
            return;
        }
        var verifyRequest = verifyRequest(request);
        if(!verifyRequest.paymentOrder().isPendingStatus()) {
            log.info("Ignored notification, PaymentOrder {} is not Pending", verifyRequest.paymentOrder().getId());
            return;
        }
        if (paymentClient.verifyNotification(verifyRequest)) {
            eventPublisherService.emit(new PaymentNotifiedEvent(request));
        }
    }

    private VerifyNotificationRequest verifyRequest(PaycometWebhookRequest request) {
        return new VerifyNotificationRequest(
                paymentOrderAppCardRepository.getByPciTransactionId(UUID.fromString(request.getOrder())),
                request.getNotificationHash(),
                request.getTransactionType(),
                request.getAmount(),
                request.getCurrency(),
                request.getBankDateTime(),
                request.getResponse()
        );
    }

}
