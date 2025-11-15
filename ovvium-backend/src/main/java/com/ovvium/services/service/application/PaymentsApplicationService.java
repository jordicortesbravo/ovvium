package com.ovvium.services.service.application;

import com.ovvium.services.model.common.MoneyAmount;
import com.ovvium.services.model.customer.Location;
import com.ovvium.services.model.payment.PaymentOrderApp;
import com.ovvium.services.model.payment.Tip;
import com.ovvium.services.service.CustomerService;
import com.ovvium.services.service.PaymentService;
import com.ovvium.services.service.UserService;
import com.ovvium.services.transfer.command.payment.AdvancePaymentAppCardCommand;
import com.ovvium.services.util.ovvium.base.Validations;
import com.ovvium.services.web.controller.bff.v1.transfer.request.payment.AdvancePaymentAppCardRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.response.payment.PaymentOrderAppCardResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.payment.PaymentOrderPosResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.payment.PaymentOrderResponse;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.ovvium.services.app.constant.Caches.PAYMENT_ORDERS;
import static com.ovvium.services.util.ovvium.base.Preconditions.check;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentsApplicationService {

    private final PaymentService paymentService;
    private final CustomerService customerService;
    private final UserService userService;

    @Cacheable(PAYMENT_ORDERS)
    public PaymentOrderResponse getPaymentOrder(UUID paymentId) {
        val paymentOrder = paymentService.getPaymentOrder(paymentId);
        if (paymentOrder instanceof PaymentOrderApp) {
            return new PaymentOrderAppCardResponse(paymentOrder.as(PaymentOrderApp.class), null);
        } else {
            return new PaymentOrderPosResponse(paymentOrder);
        }
    }

    public PaymentOrderAppCardResponse payAndOrder(AdvancePaymentAppCardRequest request) {
        Validations.validate(request);
        val customer = customerService.getCustomer(request.getCustomerId());
        val locations = customer.getLocationsById(request.getLocationIds());
        check(locations.stream().anyMatch(Location::isAdvancePayment),
                "Advance Payment for these Locations not allowed: %s".formatted(request.getLocationIds()));
        val user = userService.getAuthenticatedUser();
        val pciDetails = user.getSinglePciDetails(request.getPciDetailsId());
        return paymentService.payAndOrder(new AdvancePaymentAppCardCommand(
                customer,
                locations,
                user,
                pciDetails,
                request.getOrders(),
                request.getTipAmount()
                        .map(MoneyAmount::ofDouble)
                        .map(Tip::new)
                        .orElse(null)
        ));
    }
}
