package com.ovvium.services.web.controller.bff.v1.transfer.request.invoice;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class CreateInvoiceDateRequest {

	private LocalDate date;
	private UUID customerId;

}
