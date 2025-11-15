package com.ovvium.integration.application;

import com.ovvium.services.web.controller.bff.v1.transfer.request.user.UpdateUserRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.response.user.UserProfileResponse;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.Duration;

import static com.ovvium.integration.DbDataConstants.*;
import static com.ovvium.services.security.ApiKeyAuthenticationFilter.API_KEY_HEADER;
import static com.ovvium.services.web.controller.bff.v1.WebConstants.BASE_URI_API_V1;
import static com.ovvium.utils.SpringMockMvcUtils.doGet;
import static com.ovvium.utils.SpringMockMvcUtils.doPatch;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserApiControllerIT extends AbstractApplicationIntegrationTest {

	@Test
	public void given_not_logged_user_when_get_my_profile_then_should_throw_exception() throws Exception {
		final MockHttpServletRequestBuilder builder = get(BASE_URI_API_V1 + "/me")
				.contentType(MediaType.APPLICATION_JSON);
		builder.header(API_KEY_HEADER, API_KEY_TESTS);
		mockMvc.perform(builder)
				.andExpect(status().isUnauthorized());
	}

	@Test
	public void given_logged_user_when_get_my_profile_then_should_return_response() {
		final String accessToken = loginUser(USER_1_EMAIL, USER_1_PASSWORD);

		final UserProfileResponse userProfileResponse = doGet(mockMvc, "/me", accessToken, UserProfileResponse.class);

		assertThat(userProfileResponse.getId()).isEqualTo(USER_1_ID);
	}

	@Test
	public void given_logged_user_when_update_user_password_then_should_send_email() {
		final String accessToken = loginUser(USER_1_EMAIL, USER_1_PASSWORD);

		final UpdateUserRequest updateUserRequest = new UpdateUserRequest().setPassword(
				new UpdateUserRequest.ChangeUserPasswordRequest()
						.setOldPassword(USER_1_PASSWORD)
						.setNewPassword("newpassword")
		);

		doPatch(mockMvc,"/me", updateUserRequest, status().isOk(), accessToken, Void.class);

		await().atMost(Duration.of(5, SECONDS))
				.untilAsserted(() -> verify(mailHelper, times(1)).sendMail(any()));
	}

}
