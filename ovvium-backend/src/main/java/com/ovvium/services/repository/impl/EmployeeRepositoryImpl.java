package com.ovvium.services.repository.impl;

import com.ovvium.services.model.customer.Employee;
import com.ovvium.services.repository.EmployeeRepository;
import com.ovvium.services.util.jpa.core.JpaDefaultRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class EmployeeRepositoryImpl extends JpaDefaultRepository<Employee, UUID> implements EmployeeRepository {

}
