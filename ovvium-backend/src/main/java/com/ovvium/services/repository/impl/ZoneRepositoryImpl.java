package com.ovvium.services.repository.impl;

import com.ovvium.services.model.customer.QZone;
import com.ovvium.services.model.customer.Zone;
import com.ovvium.services.repository.ZoneRepository;
import com.ovvium.services.util.jpa.core.JpaDefaultRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class ZoneRepositoryImpl extends JpaDefaultRepository<Zone, UUID> implements ZoneRepository {

	private static final QZone qZone = QZone.zone;

	@Override
	public List<Zone> listByCustomer(UUID customerId) {
		return list(qZone.customerId.eq(customerId), qZone.name.asc());
	}
}
