package com.ovvium.services.web.controller.bff.v1;

import com.ovvium.services.security.JwtUtil;
import com.ovvium.services.service.UserService;
import com.ovvium.services.web.controller.bff.v1.transfer.request.user.UpdateUserRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.response.user.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.web.bind.annotation.*;

import static com.ovvium.services.web.controller.bff.v1.WebConstants.BASE_URI_API_V1;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = BASE_URI_API_V1, produces = APPLICATION_JSON_VALUE)
public class UserApiController {

	private final UserService userService;

	@ResponseStatus(OK)
	@PatchMapping("/me")
	public void updateCurrentUser(@RequestBody UpdateUserRequest updateUserRequest) {
		val userId = JwtUtil.getAuthenticatedUserOrFail().getId();
		updateUserRequest.setUserId(userId);
		userService.updateUser(updateUserRequest);
	}

	@ResponseStatus(OK)
	@GetMapping("/me")
	public UserProfileResponse getCurrentUser() {
		return userService.getCurrentUser();
	}

	@ResponseStatus(OK)
	@DeleteMapping("/me")
	public void removeCurrentUser() {
		userService.removeCurrentUser();
	}

}
