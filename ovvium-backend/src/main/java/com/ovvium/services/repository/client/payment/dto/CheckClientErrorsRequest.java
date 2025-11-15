package com.ovvium.services.repository.client.payment.dto;

import org.apache.commons.lang.StringUtils;

import java.util.Optional;

public record CheckClientErrorsRequest(
        String errorId,
        Integer responseId,
        boolean failOnWrongResponseId,
        Object response
) {

    public Optional<String> getErrorId() {
        String value = StringUtils.isBlank(errorId) || Integer.parseInt(errorId) == 0 ? null : errorId;
        return Optional.ofNullable(value);
    }

    public Optional<Integer> getResponseId() {
        return Optional.ofNullable(responseId);
    }
}
