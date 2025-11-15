package com.ovvium.services.repository.impl;

import com.ovvium.services.model.customer.Customer;
import com.ovvium.services.model.customer.QCustomer;
import com.ovvium.services.model.user.User;
import com.ovvium.services.repository.CustomerRepository;
import com.ovvium.services.util.jpa.core.JpaDefaultRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class CustomerRepositoryImpl extends JpaDefaultRepository<Customer, UUID> implements CustomerRepository {

	private static final QCustomer qCustomer = QCustomer.customer;

	@Override
	public Optional<Customer> getCustomerByUser(User user) {
		return get(qCustomer.adminUsers.any().eq(user));
	}

	@Override
	public boolean existsAccessCode(Customer customer, String accessCode) {
		return count(qCustomer.id.eq(customer.getId()).and(qCustomer.employees.any().accessCode.eq(accessCode))) > 0;
	}
}
