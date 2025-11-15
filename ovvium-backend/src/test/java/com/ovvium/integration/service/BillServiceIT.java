package com.ovvium.integration.service;

import com.ovvium.integration.AbstractIntegrationTest;
import com.ovvium.integration.DbDataConstants;
import com.ovvium.integration.config.ServiceTestConfig;
import com.ovvium.services.app.config.BaseConfig;
import com.ovvium.services.model.bill.Bill;
import com.ovvium.services.repository.BillRepository;
import com.ovvium.services.service.BillService;
import com.ovvium.services.service.event.EventConsumerService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@ContextConfiguration(classes = {BaseConfig.class, ServiceTestConfig.class})
@Sql(scripts = "/init_data.sql", executionPhase = BEFORE_TEST_METHOD)
public class BillServiceIT extends AbstractIntegrationTest {

	@MockBean
	private EventConsumerService eventConsumerService;

	@Autowired
	private BillService billService;

	@Autowired
	private BillRepository billRepository;

	@Test
	public void given_closed_bill_of_user_when_get_my_bill_then_return_last_bill() {
		Bill closedBill = billRepository.getOrFail(DbDataConstants.BILL_3_CLOSED_ID);

		Bill bill = billService.getCurrentBillOfUser(DbDataConstants.USER_1_ID);

		assertThat(bill).isEqualTo(closedBill);
		assertThat(bill.isClosed()).isTrue();
	}
}
