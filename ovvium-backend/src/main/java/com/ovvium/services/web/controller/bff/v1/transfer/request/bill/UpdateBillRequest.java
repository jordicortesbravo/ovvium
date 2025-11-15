package com.ovvium.services.web.controller.bff.v1.transfer.request.bill;

import com.ovvium.services.web.controller.bff.v1.transfer.request.order.UpdateOrderRequest;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Collections.emptyList;

@Data
@Accessors(chain = true)
public class UpdateBillRequest {

	private UUID billId;
	private UUID customerId;
	private UUID employeeId;
	private List<UpdateOrderRequest> orders = emptyList();

	public Optional<UUID> getEmployeeId() {
		return Optional.ofNullable(employeeId);
	}
}
