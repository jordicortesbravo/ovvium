package com.ovvium.services.web.controller.bff.v1.transfer.request.product;

import com.ovvium.services.model.bill.ServiceTime;
import com.ovvium.services.model.user.Allergen;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class CreateProductGroupRequest extends CreateProductRequest {

	private Set<DayOfWeek> daysOfWeek;

	private String startTime;

	private String endTime;

	@NotEmpty
	private Map<ServiceTime, Set<UUID>> productIds = new HashMap<>();

	private Set<Allergen> allergens = Collections.emptySet();

	public Optional<LocalTime> getStartTime() {
		return Optional.ofNullable(startTime)
				.map(LocalTime::parse);
	}

	public Optional<LocalTime> getEndTime() {
		return Optional.ofNullable(endTime)
				.map(LocalTime::parse);
	}


}
