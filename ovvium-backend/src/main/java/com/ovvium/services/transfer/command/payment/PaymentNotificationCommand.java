package com.ovvium.services.transfer.command.payment;

import com.ovvium.services.model.common.MoneyAmount;
import com.ovvium.services.model.payment.PaymentOrderApp;

import java.util.Optional;

public record PaymentNotificationCommand(
        PaymentOrderApp paymentOrderApp,
        String authCode,
        String response,
        Integer error,
        String errorDescription,
        Integer amount,
        String currency
) {

    public MoneyAmount getAmount(){
        return MoneyAmount.ofInteger(amount, currency);
    }

    public Optional<Integer> getError() {
        return Optional.ofNullable(error);
    }

    public Optional<String> getErrorDescription() {
        return Optional.ofNullable(errorDescription);
    }
}
