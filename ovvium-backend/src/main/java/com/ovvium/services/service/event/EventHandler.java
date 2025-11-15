package com.ovvium.services.service.event;

import com.ovvium.services.util.ovvium.domain.event.OvviumEvent;
import com.ovvium.services.util.util.reflection.ReflectionUtils;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

public abstract class EventHandler<T extends OvviumEvent> {

	public Class<T> getEventClass() {
		return ReflectionUtils.getConcreteClass(this.getClass());
	}

	@Transactional(propagation = REQUIRES_NEW)
	public abstract void handle(T event);
}
