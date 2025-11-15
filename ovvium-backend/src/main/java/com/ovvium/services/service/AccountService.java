package com.ovvium.services.service;

import com.ovvium.services.model.customer.Employee;
import com.ovvium.services.model.user.User;
import com.ovvium.services.web.controller.bff.v1.transfer.request.account.*;
import com.ovvium.services.web.controller.bff.v1.transfer.response.account.LoginResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.account.RecoverPasswordResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.account.RegisterResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.account.SessionResponse;

import java.util.UUID;

public interface AccountService {

	RegisterResponse register(RegisterRequest request);

	RegisterResponse verify(VerifyUserRequest request);

	LoginResponse login(LoginRequest loginRequest);

	LoginResponse authorize(AuthorizeRequest authorizeRequest);

	RecoverPasswordResponse recoverPassword(RecoverPasswordRequest recoverRequest);

	SessionResponse refreshAccessToken(String refreshToken);

	User getUser(UUID userId);

	User save(User user);

	SessionResponse generateTokens(User user, Employee employee);
}
