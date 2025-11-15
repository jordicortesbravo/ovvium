package com.ovvium.services.repository.impl;

import com.mysema.query.types.OrderSpecifier;
import com.ovvium.services.model.event.Event;
import com.ovvium.services.model.event.QEvent;
import com.ovvium.services.repository.EventRepository;
import com.ovvium.services.util.jpa.core.JpaDefaultRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import static com.mysema.query.types.Order.ASC;

@Repository
public class EventRepositoryImpl extends JpaDefaultRepository<Event, UUID> implements EventRepository {

	private static final QEvent qEvent = QEvent.event;

	@Override
	public Event getByQueueId(Long id) {
		return getOrFail(qEvent.queueId.eq(id));
	}

	@Override
	public Set<Long> getQueueIds() {
		return new LinkedHashSet<>(query()
				.orderBy(new OrderSpecifier<>(ASC, qEvent.created))
				.list(qEvent.queueId));
	}

	@Override
	@Transactional
	public Event saveInTransaction(Event event) {
		return save(event);
	}
}
