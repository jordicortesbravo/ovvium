package com.ovvium.services.repository.impl;

import com.ovvium.services.model.customer.Location;
import com.ovvium.services.model.customer.QLocation;
import com.ovvium.services.model.customer.SerialNumber;
import com.ovvium.services.model.customer.TagId;
import com.ovvium.services.repository.LocationRepository;
import com.ovvium.services.util.jpa.core.JpaDefaultRepository;
import lombok.val;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class LocationRepositoryImpl extends JpaDefaultRepository<Location, UUID> implements LocationRepository {

	private static final QLocation qLocation = QLocation.location;

	@Override
	public List<Location> list(UUID customerId) {
		return list(qLocation.customerId.eq(customerId), qLocation.zone.name.asc(), qLocation.position.asc());
	}

	@Override
	public int getLastPosition(UUID customerId, UUID zoneId) {
		val result = query().where(qLocation.customerId.eq(customerId).and(qLocation.zone.id.eq(zoneId)))
				.singleResult(qLocation.position.max());
		return result == null ? 0 : result;
	}

	@Override
	public Optional<Location> getByTagId(TagId tagId) {
		return get(qLocation.tagId.eq(tagId));
	}

	@Override
	public Optional<Location> getBySerialNumber(SerialNumber serialNumber) {
		return get(qLocation.serialNumber.eq(serialNumber));
	}
}
