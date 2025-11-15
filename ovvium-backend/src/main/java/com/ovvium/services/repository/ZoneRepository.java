package com.ovvium.services.repository;

import com.ovvium.services.model.customer.Zone;
import com.ovvium.services.util.jpa.core.DefaultRepository;

import java.util.List;
import java.util.UUID;

public interface ZoneRepository extends DefaultRepository<Zone, UUID> {

	List<Zone> listByCustomer(UUID customerId);

}
