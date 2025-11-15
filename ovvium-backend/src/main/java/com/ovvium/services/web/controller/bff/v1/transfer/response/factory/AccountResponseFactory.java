package com.ovvium.services.web.controller.bff.v1.transfer.response.factory;

import com.ovvium.services.model.customer.Customer;
import com.ovvium.services.model.customer.Employee;
import com.ovvium.services.model.user.User;
import com.ovvium.services.web.controller.bff.v1.transfer.response.account.LoginResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.account.RecoverPasswordResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.account.SessionResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.customer.EmployeeLoginResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.user.UserCustomerResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.user.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountResponseFactory {

	private final UserResponseFactory userResponseFactory;

	public LoginResponse create(User user, SessionResponse sessionResponse) {
		UserResponse userResponse = userResponseFactory.create(user);
		return new LoginResponse(userResponse, sessionResponse);
	}

	public LoginResponse create(User user) {
		UserResponse userResponse = userResponseFactory.create(user);
		return new LoginResponse(userResponse);
	}

	public LoginResponse create(User user, Customer customer, SessionResponse sessionResponse) {
		UserCustomerResponse userResponse = userResponseFactory.createUserCustomer(user, customer);
		return new LoginResponse(userResponse, sessionResponse);
	}

	public RecoverPasswordResponse createRecoverPassword(User user) {
		UserResponse userResponse = userResponseFactory.create(user);
		return new RecoverPasswordResponse(userResponse);
	}

	public EmployeeLoginResponse create(User user, Employee employee, SessionResponse sessionResponse) {
		UserResponse userResponse = userResponseFactory.create(user);
		return new EmployeeLoginResponse(userResponse, employee, sessionResponse);
	}

}
