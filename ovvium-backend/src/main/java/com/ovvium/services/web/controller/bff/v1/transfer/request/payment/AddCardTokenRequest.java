package com.ovvium.services.web.controller.bff.v1.transfer.request.payment;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class AddCardTokenRequest {

	private String token;
}
