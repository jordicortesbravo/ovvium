package com.ovvium.services.web.controller.bff.v1.transfer.request.product;

import com.ovvium.services.model.bill.ServiceTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class UpdateProductGroupRequest extends UpdateProductRequest {

	private Set<DayOfWeek> daysOfWeek;

	private String startTime;

	private String endTime;

	private Map<ServiceTime, Set<UUID>> productIds;

	public Optional<LocalTime> getStartTime() {
		return Optional.ofNullable(startTime)
				.map(LocalTime::parse);
	}

	public Optional<LocalTime> getEndTime() {
		return Optional.ofNullable(endTime)
				.map(LocalTime::parse);
	}

	public Optional<Set<DayOfWeek>> getDaysOfWeek() {
		return Optional.ofNullable(daysOfWeek);
	}

	public Optional<Map<ServiceTime, Set<UUID>>> getProductIds() {
		return Optional.ofNullable(productIds);
	}
}
