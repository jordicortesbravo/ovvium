package com.ovvium.integration.security;

import com.ovvium.integration.application.AbstractApplicationIntegrationTest;
import com.ovvium.services.util.ovvium.exception.OvviumApiError;
import com.ovvium.services.web.controller.bff.v1.transfer.request.account.LoginRequest;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static com.ovvium.services.security.ApiKeyAuthenticationFilter.API_KEY_HEADER;
import static com.ovvium.services.web.controller.bff.v1.WebConstants.BASE_URI_API_V1;
import static com.ovvium.utils.SpringMockMvcUtils.xsonFactory;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApiKeyAuthenticationFilterIT extends AbstractApplicationIntegrationTest {

	@Test
	public void given_missing_api_key_header_when_call_account_endpoint_then_should_throw_exception() throws Exception {
		MockHttpServletRequestBuilder builder = post(BASE_URI_API_V1 + "/account/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(xsonFactory.of(new LoginRequest().setEmail("email").setPassword("password")).toString());

		String contentAsString = mockMvc.perform(builder)
				.andExpect(status().isBadRequest())
				.andReturn()
				.getResponse()
				.getContentAsString();

		OvviumApiError ovviumApiError = xsonFactory.create(contentAsString).as(OvviumApiError.class);
		assertThat(ovviumApiError.getMessage()).isEqualTo("Missing Api Key Header.");
	}

	@Test
	public void given_missing_api_key_header_when_call_secured_endpoint_then_should_throw_exception() throws Exception {
		MockHttpServletRequestBuilder builder = get(BASE_URI_API_V1 + "/me")
				.header("Authorization", "Bearer any-valid-access-token")
				.contentType(MediaType.APPLICATION_JSON);

		String contentAsString = mockMvc.perform(builder)
				.andExpect(status().isBadRequest())
				.andReturn()
				.getResponse()
				.getContentAsString();

		OvviumApiError ovviumApiError = xsonFactory.create(contentAsString).as(OvviumApiError.class);
		assertThat(ovviumApiError.getMessage()).isEqualTo("Missing Api Key Header.");
	}

	@Test
	public void given_wrong_api_key_header_when_call_account_endpoint_then_should_throw_exception() throws Exception {
		final String wrongApiKey = "129N44McG2t7KUTkXd6ixy1f9816yM69";
		MockHttpServletRequestBuilder builder = post(BASE_URI_API_V1 + "/account/login")
				.header(API_KEY_HEADER, wrongApiKey)
				.contentType(MediaType.APPLICATION_JSON)
				.content(xsonFactory.of(new LoginRequest().setEmail("email").setPassword("password")).toString());

		String contentAsString = mockMvc.perform(builder)
				.andExpect(status().isForbidden())
				.andReturn()
				.getResponse()
				.getContentAsString();

		OvviumApiError ovviumApiError = xsonFactory.create(contentAsString).as(OvviumApiError.class);
		assertThat(ovviumApiError.getMessage()).isEqualTo("Invalid Api Key");
	}
}