package com.ovvium.services.web.controller.bff.v1;

import com.ovvium.services.service.AccountService;
import com.ovvium.services.web.controller.bff.v1.transfer.request.account.*;
import com.ovvium.services.web.controller.bff.v1.transfer.response.account.LoginResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.account.RecoverPasswordResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.account.RegisterResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.account.SessionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.ovvium.services.web.controller.bff.v1.WebConstants.BASE_URI_API_V1;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = BASE_URI_API_V1, produces = APPLICATION_JSON_VALUE)
public class AccountApiController {

	private final AccountService accountService;

	@ResponseStatus(CREATED)
	@PostMapping("/account/register")
	public RegisterResponse register(@RequestBody RegisterRequest request) {
		return accountService.register(request);
	}

	@ResponseStatus(OK)
	@PostMapping("/account/login")
	public LoginResponse login(@RequestBody LoginRequest loginRequest) {
		return accountService.login(loginRequest);
	}

	//FIXME I think this one is not used anywhere, check and remove
	@ResponseStatus(OK)
	@PostMapping(value = "/account/activate")
	public RegisterResponse activate(@RequestBody VerifyUserRequest activateRequest) {
		return accountService.verify(activateRequest);
	}

	@ResponseStatus(ACCEPTED)
	@PostMapping("/account/recover")
	public RecoverPasswordResponse recoverPassword(@RequestBody RecoverPasswordRequest recoverRequest) {
		return accountService.recoverPassword(recoverRequest);
	}

	@ResponseStatus(OK)
	@PostMapping("/account/authorize")
	public LoginResponse authorize(@RequestBody AuthorizeRequest authorizeRequest) {
		return accountService.authorize(authorizeRequest);
	}

	@ResponseStatus(OK)
	@PostMapping("/account/token/renew")
	public SessionResponse refreshAccessToken(@RequestBody RefreshRequest refreshRequest) {
		return accountService.refreshAccessToken(refreshRequest.getRefreshToken());
	}
}
