package com.ovvium.services.service.impl;

import com.ovvium.services.model.event.Event;
import com.ovvium.services.repository.EventRepository;
import com.ovvium.services.security.AuthenticatedUser;
import com.ovvium.services.service.EventPublisherService;
import com.ovvium.services.util.ovvium.domain.event.OvviumEvent;
import com.ovvium.services.util.ovvium.spring.SpringRequestUtils;
import com.ovvium.services.util.ovvium.spring.TransactionalUtils;
import com.ovvium.services.util.security.AuthWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.BlockingQueue;

import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EventPublisherServiceImpl implements EventPublisherService {

	private final EventRepository eventRepository;
	private final BlockingQueue<OvviumEvent> ovviumEvents;

	@Override
	public Event log(OvviumEvent ovviumEvent) {
		// This is for logging purposes, we mark it as deleted so the queue doesn´t see this event
		fillRequestData(ovviumEvent);
		return eventRepository.save(new Event(ovviumEvent).delete());
	}

	/**
	 * Emits an event to the Queue after current Transaction has finished.
	 */
	@Override
	public void emit(OvviumEvent ovviumEvent) {
		fillRequestData(ovviumEvent);
		TransactionalUtils.executeAfterCommit(() -> {
			boolean added = false;
			try {
				added = ovviumEvents.offer(ovviumEvent, 10, SECONDS);
			} catch (InterruptedException e) {
				log.warn("Offer to queue was interrupted", e);
			} finally {
				// To avoid losing not added events to Queue, so we can retry later
				if (!added) {
					eventRepository.saveInTransaction(new Event(ovviumEvent));
				} else {
					log.info("Emmited event {}", ovviumEvent.getClass().getSimpleName());
				}
			}
		});
	}

	private void fillRequestData(OvviumEvent ovviumEvent) {
		SpringRequestUtils.getRequestPath()
				.ifPresent(ovviumEvent::setRequestPath);
		new AuthWrapper().getPrincipal(AuthenticatedUser.class)
				.map(AuthenticatedUser::getId)
				.ifPresent(ovviumEvent::setRequestUserId);
	}

}
