package com.ovvium.integration.service.event;

import com.ovvium.integration.AbstractIntegrationTest;
import com.ovvium.integration.config.RepositoryTestConfig;
import com.ovvium.services.app.config.HazelcastConfig;
import com.ovvium.services.repository.EventRepository;
import com.ovvium.services.repository.client.payment.ws.PaycometWsClient;
import com.ovvium.services.service.event.EventConsumerService;
import com.ovvium.services.util.ovvium.domain.event.OvviumEvent;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.BlockingQueue;

import static com.ovvium.mother.model.OvviumEventMother.anyEvent;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@ContextConfiguration(classes = {RepositoryTestConfig.class, HazelcastConfig.class})
public class EventQueueStoreIT extends AbstractIntegrationTest {

	@MockBean
	private PaycometWsClient paycometWsClient;

	@MockBean
	private EventConsumerService eventConsumerService;

	@Autowired
	private BlockingQueue<OvviumEvent> queue;

	@Autowired
	private EventRepository eventRepository;

	@Test
	public void given_ovvium_event_when_add_event_to_queue_must_be_saved_by_event_queue_store_correctly() {
		OvviumEvent ovviumEvent = anyEvent();
		long before = eventRepository.count();

		queue.offer(ovviumEvent);

		assertThat(eventRepository.count()).isEqualTo(before + 1);
	}

	@Test
	public void given_ovvium_event_when_take_event_from_queue_must_be_remove_from_event_queue_store_correctly() throws InterruptedException {
		OvviumEvent ovviumEvent = anyEvent();
		queue.offer(ovviumEvent);
		long before = eventRepository.count();

		queue.take();

		assertThat(eventRepository.count()).isEqualTo(before-1);
	}

	@Test
	public void given_multiple_ovvium_events_when_offer_then_should_store_different_event_queue_ids() throws InterruptedException {
		OvviumEvent ovviumEvent = anyEvent();
		OvviumEvent ovviumEvent2 = anyEvent();
		OvviumEvent ovviumEvent3 = anyEvent();
		long before = eventRepository.count();
		queue.offer(ovviumEvent);
		queue.take();
		queue.offer(ovviumEvent2);
		queue.take();
		queue.offer(ovviumEvent3);

		assertThat(eventRepository.count()).isEqualTo(before + 1);
	}

}
