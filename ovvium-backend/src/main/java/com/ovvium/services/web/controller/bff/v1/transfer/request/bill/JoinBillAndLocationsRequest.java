package com.ovvium.services.web.controller.bff.v1.transfer.request.bill;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Set;
import java.util.UUID;

import static java.util.Collections.emptySet;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class JoinBillAndLocationsRequest {

	private UUID customerId;
	private UUID destinationBillId;
	private Set<UUID> locationIds = emptySet();

}
