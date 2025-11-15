package com.ovvium.integration.application;

import com.ovvium.services.repository.BillRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.ovvium.integration.DbDataConstants.*;
import static com.ovvium.utils.SpringMockMvcUtils.doDelete;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BillApiControllerIT extends AbstractApplicationIntegrationTest {

	@Autowired
	private BillRepository billRepository;

	@Test
	public void given_open_bill_when_delete_bill_then_should_close_bill() {
		final String accessToken = loginUser(CUSTOMER_2_USER_1_EMAIL, CUSTOMER_2_USER_1_PASSWORD);

		doDelete(mockMvc, "/customers/%s/bills/%s".formatted(CUSTOMER_2_ID, CUSTOMER_2_BILL_2_ID), status().isOk(), accessToken, Void.class);

		var bill = billRepository.getOrFail(CUSTOMER_2_BILL_2_ID);
		assertThat(bill.isClosed()).isTrue();
	}

}
