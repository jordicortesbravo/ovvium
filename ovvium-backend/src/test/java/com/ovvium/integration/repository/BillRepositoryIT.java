package com.ovvium.integration.repository;

import com.ovvium.integration.DbDataConstants;
import com.ovvium.services.model.bill.Bill;
import com.ovvium.services.model.bill.Order;
import com.ovvium.services.model.customer.Location;
import com.ovvium.services.model.payment.InvoiceDate;
import com.ovvium.services.model.product.ProductItem;
import com.ovvium.services.model.user.User;
import com.ovvium.services.repository.*;
import com.ovvium.services.transfer.command.order.OrderProductCommand;
import com.ovvium.services.util.util.reflection.ReflectionUtils;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static com.ovvium.integration.DbDataConstants.PRODUCT_1_ID;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

public class BillRepositoryIT extends AbstractRepositoryIT {

	private static final UUID BILL_ID = UUID.fromString("b1a42a48-b6e6-459a-9cf6-6811def090a5");

	@Autowired
	private LocationRepository locationRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private InvoiceDateRepository invoiceDateRepository;
	@Autowired
	private ProductRepository productRepository;

	// SUT
	@Autowired
	private BillRepository repository;

	@Test
	public void given_saved_bill_when_getOrFail_then_check_bill_is_saved() {
		Bill bill = createBill();

		Assertions.assertThatCode( //
				() -> repository.getOrFail(bill.getId()) //
		).doesNotThrowAnyException();
	}

	@Test
	public void given_saved_bill_when_list_then_check_bill_is_saved() {
		final List<Bill> bills = repository.list();
		final int oldSize = bills.size();

		Bill bill = createBill();

		final List<Bill> newBills = repository.list();

		assertThat(newBills.size()).isEqualTo(oldSize + 1);
		assertThat(newBills.get(oldSize).getId()).isEqualTo(bill.getId());
	}


	@Test
	public void given_saved_bill_with_deleted_orders_when_getOrFail_then_check_orders_are_not_retrieved() {
		Bill bill = createBill();
		Order order = bill.createOrder(new OrderProductCommand(
				null,
				productRepository.getOrFail(PRODUCT_1_ID).as(ProductItem.class),
				null,
				null,
				null,
				null
		));
		bill.deleteOrder(order.getId());
		repository.save(bill);

		// it needs to be flushed to DB and clear caches
		entityManager.flush();
		entityManager.clear();

		Bill savedBill = repository.getOrFail(bill.getId());

		assertThat(savedBill.getOrders()).isEmpty();
	}

	@Test
	public void given_saved_bill_with_not_deleted_orders_when_getOrFail_then_check_orders_are_retrieved() {
		Bill bill = createBill();
		Order order = bill.createOrder(new OrderProductCommand(
				null,
				productRepository.getOrFail(PRODUCT_1_ID).as(ProductItem.class),
				null,
				null,
				null,
				null
		));
		repository.save(bill);

		// it needs to be flushed to DB and clear caches
		entityManager.flush();
		entityManager.clear();

		Bill savedBill = repository.getOrFail(bill.getId());

		assertThat(savedBill.getOrders()).hasSize(1);
	}

	private Bill createBill() {
		final Location location = locationRepository.getOrFail(DbDataConstants.LOCATION_1_ID);
		final User user = userRepository.getOrFail(DbDataConstants.USER_1_ID);
		final InvoiceDate invoiceDate = invoiceDateRepository.getOrFail(DbDataConstants.INVOICE_DATE_CUSTOMER_1_ID);
		final Bill bill = new Bill(invoiceDate, user, singletonList(location));
		ReflectionUtils.set(bill, "id", BILL_ID);
		return repository.save(bill);
	}

}
