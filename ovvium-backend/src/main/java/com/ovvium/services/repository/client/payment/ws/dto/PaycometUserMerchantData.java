package com.ovvium.services.repository.client.payment.ws.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ovvium.services.model.user.User;

import java.util.UUID;

import static com.ovvium.services.util.ovvium.string.OvviumStringUtils.EMAIL_FAKE_DOMAIN;

public record PaycometUserMerchantData(
        @JsonProperty("customer")
        PaycometUserMerchantDataCustomer customer
) {

    public record PaycometUserMerchantDataCustomer(
            @JsonProperty("id")
            UUID id,
            @JsonProperty("name")
            String name,
            @JsonProperty("surname")
            String surname,
            @JsonProperty("email")
            String email
    ) {
    }

    public PaycometUserMerchantData(User user) {
        this(new PaycometUserMerchantDataCustomer(
                user.getId(),
                user.getFirstName(),
                user.getSurnames(),
                user.getEmail().contains(EMAIL_FAKE_DOMAIN) ? null : user.getEmail()
        ));
    }


}
