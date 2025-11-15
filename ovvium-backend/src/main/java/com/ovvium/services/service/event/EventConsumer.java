package com.ovvium.services.service.event;

import com.ovvium.services.util.ovvium.domain.event.OvviumEvent;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Slf4j
public class EventConsumer implements Runnable {

	private final BlockingQueue<OvviumEvent> queue;
	private final Map<Class<? extends OvviumEvent>, List<EventHandler<?>>> handlers;

	public EventConsumer(BlockingQueue<OvviumEvent> queue, List<EventHandler<?>> handlers) {
		this.queue = queue;
		this.handlers = handlers.stream().collect(Collectors.groupingBy(EventHandler::getEventClass));
	}

	@Override
	public void run() {
		log.info("Starting Event Consumer");
		while (true) {
			try {
				val ovviumEvent = queue.take();

				val concreteClass = ovviumEvent.getClass();
				val handlers = Optional.ofNullable(this.handlers.get(concreteClass));
				if (handlers.isPresent()) {
					//TODO Revisar que el evento in memory no es DELETED dentro de la transaccion
					handlers.get().forEach(handler -> handle(handler, ovviumEvent));
				} else {
					log.error("No handlers exists for this event {}", concreteClass.getSimpleName());
				}
			} catch (InterruptedException e) {
				throw new RuntimeException("Queue take was interrupted", e);
			}
		}
	}

	private <T extends OvviumEvent> void handle(EventHandler<T> handler, OvviumEvent ovviumEvent) {
		Class<T> eventClass = handler.getEventClass();
		try {
			handler.handle(eventClass.cast(ovviumEvent));
			log.debug("Event {} correctly handled by handler {}", eventClass.getSimpleName(), handler.getClass().getSimpleName());
		} catch (Exception exc) {
			log.error(format("Error handling event %s by handler %s", eventClass.getSimpleName(), handler.getClass().getSimpleName()), exc);
		}
	}

}
