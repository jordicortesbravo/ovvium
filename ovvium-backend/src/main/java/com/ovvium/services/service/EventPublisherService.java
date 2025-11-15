package com.ovvium.services.service;

import com.ovvium.services.model.event.Event;
import com.ovvium.services.util.ovvium.domain.event.OvviumEvent;

public interface EventPublisherService {

	Event log(OvviumEvent event);

	void emit(OvviumEvent ovviumEvent);
}
