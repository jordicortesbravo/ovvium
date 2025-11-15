package com.ovvium.services.repository;

import com.ovvium.services.model.customer.Location;
import com.ovvium.services.model.customer.SerialNumber;
import com.ovvium.services.model.customer.TagId;
import com.ovvium.services.util.jpa.core.DefaultRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LocationRepository extends DefaultRepository<Location, UUID> {

	List<Location> list(UUID customerId);

	int getLastPosition(UUID customerId, UUID zoneId);

	Optional<Location> getByTagId(TagId tagId);

	Optional<Location> getBySerialNumber(SerialNumber serialNumber);
}
