package com.ovvium.integration.application;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ovvium.mother.model.PaycometMother;
import com.ovvium.services.model.common.MoneyAmount;
import com.ovvium.services.repository.PaymentOrderAppCardRepository;
import com.ovvium.services.repository.client.payment.PaymentClient;
import com.ovvium.services.web.controller.bff.v1.transfer.request.webhooks.paycomet.PaycometWebhookRequest;
import lombok.SneakyThrows;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.HashMap;

import static com.ovvium.integration.DbDataConstants.PAYMENT_ORDER_PENDING_PCI_TRANSACTION_ID;
import static com.ovvium.services.model.payment.PaymentOrderStatus.CONFIRMED;
import static com.ovvium.services.repository.client.payment.ws.dto.PaycometTransactionType.AUTHORIZATION;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class WebhooksApiControllerIT extends AbstractApplicationIntegrationTest {

	private static final String BASE_WEBHOOKS_URL = "/webhooks";

	private final ObjectMapper objectMapper = new ObjectMapper();

	@MockBean
	private PaymentClient paymentClient;

	@Autowired
	private PaymentOrderAppCardRepository paymentOrderAppCardRepository;


	@Test
	public void given_call_when_call_to_paycometWebhookListener_endpoint_then_should_not_be_secured() throws Exception {
		when(paymentClient.verifyNotification(Mockito.any())).thenReturn(false);

		final MockHttpServletRequestBuilder builder = post(BASE_WEBHOOKS_URL + "/payments/pc")
				.content(aCorrectPaycometNotification())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE);
		mockMvc.perform(builder)
				.andExpect(status().isOk());
	}

	@Test
	public void given_call_when_call_to_paycometWebhookListener_endpoint_then_event_should_be_handled() throws Exception {
		when(paymentClient.verifyNotification(Mockito.any())).thenReturn(true);
		when(paymentClient.splitTransfer(Mockito.any())).thenReturn(PaycometMother.splitTransferResponse(MoneyAmount.ofDouble(200)));

		final MockHttpServletRequestBuilder builder = post(BASE_WEBHOOKS_URL + "/payments/pc")
				.content(aCorrectPaycometNotification())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE);
		mockMvc.perform(builder)
				.andExpect(status().isOk());

		await()
				.untilAsserted(() ->
						assertThat(paymentOrderAppCardRepository.getByPciTransactionId(PAYMENT_ORDER_PENDING_PCI_TRANSACTION_ID).getStatus())
								.isEqualTo(CONFIRMED)
				);
	}

	@SneakyThrows
	private String aCorrectPaycometNotification() {
		var request = new PaycometWebhookRequest();
		request.setOrder(PAYMENT_ORDER_PENDING_PCI_TRANSACTION_ID.toString());
		request.setAmount(200);
		request.setCurrency("EUR");
		request.setAuthCode("authcode");
		request.setTransactionType(AUTHORIZATION.getValue());
		request.setAccountCode("8989");
		request.setNotificationHash("hash");

		TypeReference<HashMap<String, String>> typeRef = new TypeReference<>() {
		};
		// form urlencoded format
		return EntityUtils.toString(new UrlEncodedFormEntity(
				objectMapper.readValue(objectMapper.writeValueAsString(request), typeRef).entrySet().stream().map((entry) ->
						new BasicNameValuePair(entry.getKey(), entry.getValue())
				).collect(toList())
		));
	}

}
