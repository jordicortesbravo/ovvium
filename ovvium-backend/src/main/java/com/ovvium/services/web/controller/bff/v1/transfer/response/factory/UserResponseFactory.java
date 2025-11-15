package com.ovvium.services.web.controller.bff.v1.transfer.response.factory;

import com.ovvium.services.model.customer.Customer;
import com.ovvium.services.model.user.User;
import com.ovvium.services.web.controller.bff.v1.transfer.response.picture.PictureResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.user.UserCustomerResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.user.UserProfileResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.user.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;

import java.net.URI;

import static com.ovvium.services.model.product.PictureSize.LOW;

@Component
@RequiredArgsConstructor
public class UserResponseFactory {

	private final PictureResponseFactory pictureResponseFactory;

	public UserResponse create(User user) {
		val pictureUri = getPictureUri(user);
		return new UserResponse(user, pictureUri);
	}

	public UserProfileResponse createUserProfile(User user) {
		val pictureUri = getPictureUri(user);
		return new UserProfileResponse(user, pictureUri);
	}

	public UserCustomerResponse createUserCustomer(User user, Customer customer) {
		val pictureUri = getPictureUri(user);
		return new UserCustomerResponse(user, pictureUri, customer);
	}

	private URI getPictureUri(User user) {
		return user.getPicture().map(pic ->
				pictureResponseFactory.getCropsResponses(pic).get(LOW.name().toLowerCase())
		).map(PictureResponse::getUrl).orElse(null);
	}

}
