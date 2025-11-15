package com.ovvium.services.web.controller.bff.v1.transfer.response.user;

import com.ovvium.services.model.user.Allergen;
import com.ovvium.services.model.user.FoodPreference;
import com.ovvium.services.model.user.User;
import com.ovvium.services.web.controller.bff.v1.transfer.response.common.ResourceIdResponse;
import lombok.Getter;

import java.net.URI;
import java.util.Set;

@Getter
public class UserProfileResponse extends ResourceIdResponse {

	private final String name;
	private final URI imageUri;
	private final Set<Allergen> allergens;
	private final Set<FoodPreference> foodPreferences;

	public UserProfileResponse(User user, URI pictureUri) {
		super(user);
		this.name = user.getName();
		this.allergens = user.getAllergens();
		this.foodPreferences = user.getFoodPreferences();
		this.imageUri = pictureUri;
	}

}
