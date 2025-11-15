package com.ovvium.services.repository.client.payment;

import com.ovvium.services.model.payment.UnsuccessfulPaymentClientException;
import com.ovvium.services.repository.client.payment.dto.*;
import lombok.RequiredArgsConstructor;

import static com.ovvium.services.service.populator.PopulatorConstants.TEST_CUSTOMERS;
import static com.ovvium.services.service.populator.PopulatorConstants.TEST_USERS;

/**
 * This PaymentClientDecider will decide between using a local PaymentClient mock
 * if we use test users or test customers, or the external PaymentClient.
 */
@RequiredArgsConstructor
public class PaymentClientDecider implements PaymentClient {

    private final PaymentClient localClient;
    private final PaymentClient externalClient;

    @Override
    public AddUserTokenResponse addUserToken(AddUserTokenRequest request) {
        if (TEST_USERS.contains(request.user().getId())) {
            return localClient.addUserToken(request);
        }
        return externalClient.addUserToken(request);
    }

    @Override
    public RemoveUserTokenResponse removeUserToken(RemoveUserTokenRequest request) {
        if (TEST_USERS.contains(request.user().getId())) {
            return localClient.removeUserToken(request);
        }
        return externalClient.removeUserToken(request);
    }

    @Override
    public ExecutePurchaseResponse executePurchase(ExecutePurchaseRequest request) {
        if (TEST_CUSTOMERS.contains(request.customerId())) {
            return localClient.executePurchase(request);
        }
        return externalClient.executePurchase(request);
    }

    @Override
    public InfoUserResponse getInfoUser(InfoUserRequest request) {
        if (TEST_USERS.contains(request.user().getId())) {
            return localClient.getInfoUser(request);
        }
        return externalClient.getInfoUser(request);
    }

    @Override
    public SplitTransferResponse splitTransfer(SplitTransferRequest request) {
        if (TEST_CUSTOMERS.contains(request.customer().getId())) {
            return localClient.splitTransfer(request);
        }
        return externalClient.splitTransfer(request);
    }

    @Override
    public boolean verifyNotification(VerifyNotificationRequest request) {
        if (TEST_CUSTOMERS.contains(request.paymentOrder().getBill().getCustomerId())) {
            return localClient.verifyNotification(request);
        }
        return externalClient.verifyNotification(request);
    }

    @Override
    public void checkForErrors(CheckClientErrorsRequest request) throws UnsuccessfulPaymentClientException {
        externalClient.checkForErrors(request);
    }
}
