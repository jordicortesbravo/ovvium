package com.ovvium.services.web.controller.bff.v1.transfer.request.payment;

import com.ovvium.services.web.controller.bff.v1.transfer.request.order.CreateOrderRequest;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static java.util.Collections.emptyList;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class AdvancePaymentAppCardRequest extends PaymentRequest {

    @NotNull
    private UUID pciDetailsId;

    @NotNull
    private UUID customerId;

    @NotEmpty
    private Set<UUID> locationIds;

    private List<CreateOrderRequest> orders = emptyList();

}
