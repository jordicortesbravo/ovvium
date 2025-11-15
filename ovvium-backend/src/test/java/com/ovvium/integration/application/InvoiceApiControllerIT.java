package com.ovvium.integration.application;

import com.google.common.collect.Sets;
import com.ovvium.services.model.common.MoneyAmount;
import com.ovvium.services.web.controller.bff.v1.transfer.request.payment.PaymentAppCardRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.payment.PaymentRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.response.invoice.InvoicePageResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.payment.PaymentOrderAppCardResponse;
import org.junit.Test;

import java.util.UUID;

import static com.ovvium.integration.DbDataConstants.*;
import static com.ovvium.mother.model.PaycometMother.anyCorrectInfoUserWsResponse;
import static com.ovvium.mother.model.PaycometMother.executePurchaseWsResponse;
import static com.ovvium.utils.SpringMockMvcUtils.doGet;
import static com.ovvium.utils.SpringMockMvcUtils.doPost;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class InvoiceApiControllerIT extends AbstractApplicationIntegrationTest {

	@Test
	public void given_logged_user_when_get_my_invoices_then_should_return_invoices_of_user() {
		final String accessToken = loginUser(USER_2_EMAIL, USER_2_PASSWORD);
		final PaymentRequest request = new PaymentAppCardRequest()
				.setPciDetailsId(USER_2_PCI_DETAILS_ID)
				.setOrderIds(Sets.newHashSet(ORDER_1_BILL_1_ID, ORDER_2_BILL_1_ID))
				.setTipAmount(2d);
		when(paycometWsClient.executePurchase(any())).thenReturn(executePurchaseWsResponse(MoneyAmount.ofDouble(2)));
		when(paycometWsClient.getInfoUser(any())).thenReturn(anyCorrectInfoUserWsResponse());
		UUID invoiceId = doPost(mockMvc, "/payments/app-card", request, status().isCreated(), accessToken, PaymentOrderAppCardResponse.class)
				.getInvoiceId();

		final InvoicePageResponse response = doGet(mockMvc, "/me/invoices", accessToken, InvoicePageResponse.class);

		assertThat(response.getContent()).isNotEmpty();
		assertThat(response.getContent().get(0).getId()).isEqualTo(invoiceId);
		assertThat(response.getContent().get(0).getUser().getEmail()).isEqualTo(USER_2_EMAIL);
	}

}
