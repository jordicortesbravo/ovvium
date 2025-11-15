package com.ovvium.services.web.controller.bff.v1.transfer.request.payment;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

import static java.util.Collections.emptySet;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class PaymentAppCardRequest extends PaymentRequest {

    private Set<@NotNull UUID> orderIds = emptySet();

    @NotNull
    private UUID pciDetailsId;

}
