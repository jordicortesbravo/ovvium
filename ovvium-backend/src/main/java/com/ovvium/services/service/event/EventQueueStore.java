package com.ovvium.services.service.event;

import com.hazelcast.collection.QueueStore;
import com.ovvium.services.model.event.Event;
import com.ovvium.services.repository.EventRepository;
import com.ovvium.services.util.ovvium.domain.event.OvviumEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Persist and retrieve OvviumEvents used by Hazelcast Queue.
 */
@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class EventQueueStore implements QueueStore<OvviumEvent> {

	private final EventRepository eventRepository;

	@Override
	public void store(Long eventId, OvviumEvent event) {
		val entity = eventRepository.save(new Event(event).setQueueId(eventId));
		log.debug("Event stored {}", entity.getId());
	}

	@Override
	public void storeAll(Map<Long, OvviumEvent> map) {
		map.forEach((key, value) -> eventRepository.save(new Event(value).setQueueId(key)));
	}

	@Override
	public void delete(Long eventId) {
		Event event = eventRepository.getByQueueId(eventId);
		eventRepository.save(event.delete());
		log.debug("Event deleted {}", event.getId());
	}

	@Override
	public void deleteAll(Collection<Long> eventIds) {
		eventIds.forEach(this::delete);
	}

	@Override
	public OvviumEvent load(Long eventId) {
		return eventRepository.getByQueueId(eventId).asOvviumEvent();
	}

	@Override
	public Map<Long, OvviumEvent> loadAll(Collection<Long> eventIds) {
		return eventIds.stream()
				.map(eventRepository::getByQueueId)
				.collect(Collectors.toMap(e -> e.getQueueId().get(), Event::asOvviumEvent));
	}

	@Override
	public Set<Long> loadAllKeys() {
		return eventRepository.getQueueIds();
	}

}
