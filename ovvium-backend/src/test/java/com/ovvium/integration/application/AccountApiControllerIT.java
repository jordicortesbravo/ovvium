package com.ovvium.integration.application;

import com.ovvium.services.web.controller.bff.v1.transfer.request.account.LoginRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.account.RegisterRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.response.account.LoginResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.account.RegisterResponse;
import org.junit.Test;

import java.time.Duration;
import java.time.ZonedDateTime;

import static com.ovvium.integration.DbDataConstants.*;
import static com.ovvium.utils.SpringMockMvcUtils.doPost;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AccountApiControllerIT extends AbstractApplicationIntegrationTest {

	@Test
	public void given_not_logged_user_when_login_then_should_return_login_response() {
		LoginResponse loginResponse = doPost(mockMvc, "/account/login",
				new LoginRequest().setEmail(USER_1_EMAIL).setPassword(USER_1_PASSWORD),
				status().is2xxSuccessful(),
				null,
				LoginResponse.class);

		assertThat(loginResponse.getUser().getId()).isEqualTo(USER_1_ID);
		assertThatCode(() ->
				ZonedDateTime.parse(loginResponse.getSession().getLoggedUntil())
		).doesNotThrowAnyException();
	}

	@Test
	public void given_new_user_when_register_user_then_should_send_activation_email() {
		doPost(mockMvc, "/account/register",
				new RegisterRequest().setEmail("newuser@ovvium.com").setPassword(USER_1_PASSWORD).setName("New User"),
				status().isCreated(),
				null,
				RegisterResponse.class);

		await().atMost(Duration.of(5, SECONDS))
				.untilAsserted(() -> verify(mailHelper, times(1)).sendMail(any()));
	}


}
