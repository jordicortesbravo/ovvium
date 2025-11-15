package com.ovvium.mother.builder;

import com.ovvium.mother.model.CustomerMother;
import com.ovvium.mother.model.UserMother;
import com.ovvium.services.model.customer.CommissionConfig;
import com.ovvium.services.model.customer.Customer;
import com.ovvium.services.model.customer.InvoiceNumberPrefix;
import com.ovvium.services.model.user.User;
import com.ovvium.services.util.util.reflection.ReflectionUtils;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.ZoneId;
import java.util.Set;
import java.util.UUID;

import static java.util.Collections.singleton;

@Setter
@Accessors(chain = true)
public class CustomerBuilder {

	private UUID id = CustomerMother.EL_BULLI_CUSTOMER_ID;
	private User user = UserMother.getCustomerUserFAdria();
	private String name = "El Bulli";
	private String description = "Restaurant de F Adria";
	private String cif = "12345678X";
	private String address = "C/ Barcelona 2";
	private Set<String> phones = singleton("666666666");
	private String pciSplitUserId = "abcdefg";
	private CommissionConfig commissionConfig = CommissionConfig.cardCategory(0.5, 0.005, 0.09);
	private InvoiceNumberPrefix invoiceNumberPrefix = new InvoiceNumberPrefix("ELB");
	private ZoneId timeZone = ZoneId.of("Europe/Madrid");

	public Customer build() {
		Customer customer = new Customer(
				user,
				name,
				description,
				cif,
				address,
				phones,
				pciSplitUserId,
				commissionConfig,
				invoiceNumberPrefix
		);
		customer.setTimeZone(timeZone);
		ReflectionUtils.set(customer, "id", id);
		return customer;
	}

}
