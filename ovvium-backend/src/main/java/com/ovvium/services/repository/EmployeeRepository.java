package com.ovvium.services.repository;

import com.ovvium.services.model.customer.Employee;
import com.ovvium.services.util.jpa.core.DefaultRepository;

import java.util.UUID;

public interface EmployeeRepository extends DefaultRepository<Employee, UUID> {

}
