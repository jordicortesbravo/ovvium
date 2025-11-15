package com.ovvium.services.service;

import com.ovvium.services.model.user.User;
import com.ovvium.services.web.controller.bff.v1.transfer.request.user.UpdateUserRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.response.user.UserProfileResponse;

import java.util.UUID;

public interface UserService {

	User save(User user);

	User getUserOrFail(UUID userId);

	User getAuthenticatedUser();

	void updateUser(UpdateUserRequest request);

	UserProfileResponse getCurrentUser();

	void removeCurrentUser();
}
