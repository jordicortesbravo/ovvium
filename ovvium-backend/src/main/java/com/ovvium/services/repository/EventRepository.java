package com.ovvium.services.repository;

import com.ovvium.services.model.event.Event;
import com.ovvium.services.util.jpa.core.DefaultRepository;

import java.util.Set;
import java.util.UUID;

public interface EventRepository extends DefaultRepository<Event, UUID> {

	Event getByQueueId(Long id);

	Set<Long> getQueueIds();

	Event saveInTransaction(Event event);

}
