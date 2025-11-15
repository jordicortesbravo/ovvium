package com.ovvium.services.repository.client.payment.ws.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaycometTransactionType {
    AUTHORIZATION(1),
    SPLIT_TRANSFER(22);

    private final int value;
}
