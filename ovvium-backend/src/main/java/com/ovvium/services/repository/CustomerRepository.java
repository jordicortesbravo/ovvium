package com.ovvium.services.repository;

import com.ovvium.services.model.customer.Customer;
import com.ovvium.services.model.user.User;
import com.ovvium.services.util.jpa.core.DefaultRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends DefaultRepository<Customer, UUID> {

	Optional<Customer> getCustomerByUser(User user);

	boolean existsAccessCode(Customer customer, String accessCode);

}
