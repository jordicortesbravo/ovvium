package com.ovvium.services.security;

import com.ovvium.services.service.CustomerService;
import com.ovvium.services.service.RatingService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.ovvium.services.app.constant.Roles.OVVIUM_ADMIN;
import static com.ovvium.services.util.ovvium.base.Preconditions.checkNotNull;

@Component("auth")
@Transactional
@RequiredArgsConstructor
public class UserAuthorizationChecker {

	private final CustomerService customerService;
	private final RatingService ratingService;

	/**
	 * User on request path is same as loggedin User.
	 */
	public boolean isSameUser(UUID userId) {
		checkNotNull(userId,  "User ID cannot be null");
		val user = JwtUtil.getAuthenticatedUserOrFail();
		return user.getId().equals(userId) || user.getRoles().contains(OVVIUM_ADMIN);
	}

	/**
	 * User in request is from same Customer.
	 */
	public boolean isFromCustomer(UUID customerId) {
		checkNotNull(customerId,  "Customer ID cannot be null");
		val userId = JwtUtil.getAuthenticatedUserOrFail().getId();
		return customerService.getCustomer(customerId).isAdminUser(userId);
	}

	/**
	 * Rating is from loggedin User.
	 */
	public boolean isLoggedUserRating(UUID ratingId) {
		checkNotNull(ratingId,  "Rating ID cannot be null");
		val user = JwtUtil.getAuthenticatedUserOrFail();
		return ratingService.getRating(ratingId).getUserId().equals(user.getId());
	}
}