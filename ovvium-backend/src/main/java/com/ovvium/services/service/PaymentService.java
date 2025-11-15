package com.ovvium.services.service;

import com.ovvium.services.model.payment.PaymentOrder;
import com.ovvium.services.transfer.command.payment.AdvancePaymentAppCardCommand;
import com.ovvium.services.transfer.command.payment.PaymentNotificationCommand;
import com.ovvium.services.web.controller.bff.v1.transfer.request.payment.AddCardTokenRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.payment.PaymentAppCardRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.payment.PaymentInvoiceRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.response.common.ResourceIdResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.payment.PaymentOrderAppCardResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.payment.UserCardDataResponse;

import java.util.List;
import java.util.UUID;

public interface PaymentService {

	PaymentOrderAppCardResponse pay(PaymentAppCardRequest paymentRequest);

	ResourceIdResponse pay(PaymentInvoiceRequest request);

	PaymentOrderAppCardResponse payAndOrder(AdvancePaymentAppCardCommand command);

	void updatePaymentOnNotification(PaymentNotificationCommand command);

	PaymentOrder getPaymentOrder(UUID id);

	ResourceIdResponse addCardToken(AddCardTokenRequest request);

	void removeCardToken(UUID pciDetailsId);

	List<UserCardDataResponse> getCardsOfCurrentUser();

	void executeSplitTransfer(UUID paymentOrderId);

	void save(PaymentOrder paymentOrder);
}
