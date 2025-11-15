package com.ovvium.services.repository.client.payment;

import com.ovvium.services.model.payment.UnsuccessfulPaymentClientException;
import com.ovvium.services.repository.client.payment.dto.*;
import lombok.extern.slf4j.Slf4j;

import static com.ovvium.services.util.ovvium.string.OvviumStringUtils.randomUniqueString;

@Slf4j
public class LocalPaymentClientImpl implements PaymentClient {

    @Override
    public AddUserTokenResponse addUserToken(AddUserTokenRequest request) {
        log.info("DUMMY CLIENT: Added user token for jet token " + request.jetToken());
        return new AddUserTokenResponse(randomUniqueString(), randomUniqueString());
    }

    @Override
    public RemoveUserTokenResponse removeUserToken(RemoveUserTokenRequest request) {
        log.info("DUMMY CLIENT: Removed user token for user token " + request.userToken());
        return new RemoveUserTokenResponse(1);
    }

    @Override
    public ExecutePurchaseResponse executePurchase(ExecutePurchaseRequest request) {
        log.info("DUMMY CLIENT: Executed payment for Customer {} of {} amount", request.customerId(), request.amount());
        return new ExecutePurchaseResponse(
                request.amount().asInt(),
                request.orderId().toString(),
                request.amount().getCurrency().getCurrencyCode(),
                randomUniqueString(),
                1,
                1,
                null
        );
    }

    @Override
    public InfoUserResponse getInfoUser(InfoUserRequest request) {
        log.info("DUMMY CLIENT: Get info user with user id " + request.user().getId());
        return new InfoUserResponse(
                "4111-XXXX-XXXX-1111",
                "VISA",
                "CREDIT",
                "ESP",
                "2021/06",
                randomUniqueString(),
                "BUSINESS",
                1
        );
    }

    @Override
    public SplitTransferResponse splitTransfer(SplitTransferRequest request) {
        log.info("DUMMY CLIENT: Executed split transfer for Customer {} of {} amount", request.customer().getId(), request.submerchantAmount());
        return new SplitTransferResponse(
                request.submerchantAmount().asInt(),
                request.order().toString(),
                request.submerchantAmount().getCurrency().getCurrencyCode(),
                randomUniqueString(),
                1
        );
    }

    @Override
    public boolean verifyNotification(VerifyNotificationRequest request) {
        return true;
    }

    @Override
    public void checkForErrors(CheckClientErrorsRequest request) throws UnsuccessfulPaymentClientException {
        // NOOP
    }
}
