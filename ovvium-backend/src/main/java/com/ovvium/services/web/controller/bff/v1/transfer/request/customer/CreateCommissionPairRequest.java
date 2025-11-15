package com.ovvium.services.web.controller.bff.v1.transfer.request.customer;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreateCommissionPairRequest {

    private double basePercentage;
    private double commission;

}