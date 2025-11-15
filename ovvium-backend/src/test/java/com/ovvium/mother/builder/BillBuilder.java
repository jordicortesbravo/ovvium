package com.ovvium.mother.builder;

import com.ovvium.mother.model.CustomerMother;
import com.ovvium.mother.model.InvoiceDateMother;
import com.ovvium.mother.model.UserMother;
import com.ovvium.services.model.bill.Bill;
import com.ovvium.services.model.customer.Location;
import com.ovvium.services.model.payment.InvoiceDate;
import com.ovvium.services.model.user.User;
import com.ovvium.services.util.util.reflection.ReflectionUtils;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.ovvium.mother.model.BillMother.OPENED_BILL_BULLI_ID;
import static com.ovvium.mother.model.LocationMother.getElBulliFirstFreeLocation;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

@Setter
@Accessors(chain = true)
public class BillBuilder {

	private UUID id = OPENED_BILL_BULLI_ID;
	private InvoiceDate invoiceDate = InvoiceDateMother.anyInvoiceDate(LocalDate.now());
	private User user = UserMother.getUserJorge();
	private List<Location> locations = singletonList(getElBulliFirstFreeLocation());
	private List<OrderBuilder> orders = emptyList();
	private UUID customerId = CustomerMother.EL_BULLI_CUSTOMER_ID;

	public Bill build() {
		Bill bill = new Bill(invoiceDate, user, locations);
		orders.stream().map(ob -> ob.setBill(bill)).forEach(OrderBuilder::build); // vaya ida de olla
		ReflectionUtils.set(bill, "id", id);
		ReflectionUtils.set(bill, "customerId", customerId);
		return bill;
	}
}
