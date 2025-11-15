package com.ovvium.services.web.controller.bff.v1.transfer.request.bill;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JoinBillRequest {

	private String billId;
	private Long userId;

}
