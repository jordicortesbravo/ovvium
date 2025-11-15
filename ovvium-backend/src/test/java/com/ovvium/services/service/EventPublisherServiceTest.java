package com.ovvium.services.service;

import com.ovvium.mother.model.UserMother;
import com.ovvium.services.model.event.Event;
import com.ovvium.services.model.user.User;
import com.ovvium.services.repository.EventRepository;
import com.ovvium.services.security.AuthenticatedUser;
import com.ovvium.services.service.impl.EventPublisherServiceImpl;
import com.ovvium.services.util.ovvium.domain.event.OvviumEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.BlockingQueue;

import static com.ovvium.mother.model.OvviumEventMother.anyEvent;
import static com.ovvium.mother.model.UserMother.USER_JORGE_ID;
import static com.ovvium.utils.MockitoUtils.mockRepository;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EventPublisherServiceTest {

	private final static String ANY_REQUEST = "/customers";

	// SUT
	private EventPublisherService eventPublisherService;

	@Before
	public void setUp() throws Exception {
		EventRepository eventRepository = mockRepository(EventRepository.class);
		BlockingQueue queue = mock(BlockingQueue.class);
		eventPublisherService = new EventPublisherServiceImpl(eventRepository, queue);
	}

	@After
	public void cleanUp() throws Exception {
		SecurityContextHolder.getContext().setAuthentication(null);
		RequestContextHolder.resetRequestAttributes();
	}

	@Test
	public void given_ovvium_event_and_logged_user_when_publish_then_must_create_event_with_current_user_id() {
		User user = UserMother.getUserJorge();
		SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(new AuthenticatedUser(user), null));

		Event event = eventPublisherService.log(createAnyDto());

		assertThat(event.getUserId()).contains(USER_JORGE_ID);
	}

	@Test
	public void given_ovvium_event_and_not_logged_user_when_publish_then_must_create_event_without_current_user_id() {
		Event event = eventPublisherService.log(createAnyDto());

		assertThat(event.getUserId()).isEmpty();
	}

	@Test
	public void given_ovvium_event_and_no_request_path_when_publish_then_must_create_event_without_current_request_path() {
		Event event = eventPublisherService.log(createAnyDto());

		assertThat(event.getPath()).isEmpty();
	}

	@Test
	public void given_ovvium_event_and_request_path_when_publish_then_must_create_event_with_current_request_path() {
		mockRequestContextHolder();

		Event event = eventPublisherService.log(createAnyDto());

		assertThat(event.getPath()).contains(ANY_REQUEST);
	}

	private void mockRequestContextHolder() {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getRequestURI()).thenReturn(ANY_REQUEST);
		when(request.getContextPath()).thenReturn("/");
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
	}

	private OvviumEvent createAnyDto() {
		return anyEvent();
	}
}