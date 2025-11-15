package com.ovvium.services.service.event;

import com.ovvium.services.util.ovvium.domain.event.OvviumEvent;
import lombok.RequiredArgsConstructor;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiredArgsConstructor
public class EventConsumerService {

	private final BlockingQueue<OvviumEvent> queue;
	private final List<EventHandler<?>> handlers;
	private final ExecutorService executorService = Executors.newSingleThreadExecutor();

	@PostConstruct
	public void init() {
		// Maybe this can be done using N threads...
		executorService.execute(new EventConsumer(queue, handlers));
	}

	public void shutdown() {
		executorService.shutdownNow();
	}

}
