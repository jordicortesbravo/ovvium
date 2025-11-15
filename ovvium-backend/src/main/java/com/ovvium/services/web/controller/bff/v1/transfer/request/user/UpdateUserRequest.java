package com.ovvium.services.web.controller.bff.v1.transfer.request.user;

import com.ovvium.services.model.user.Allergen;
import com.ovvium.services.model.user.FoodPreference;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class UpdateUserRequest {

	@Data
	@Accessors(chain = true)
	public static class ChangeUserPasswordRequest {

		private String oldPassword;
		private String newPassword;

	}

	private UUID userId;
	private String name;
	private UUID pictureId;
	private Set<Allergen> allergens;
	private Set<FoodPreference> foodPreferences;
	private ChangeUserPasswordRequest password;

	public Optional<String> getName() {
		return Optional.ofNullable(name);
	}

	public Optional<Set<Allergen>> getAllergens() {
		return Optional.ofNullable(allergens);
	}
	public Optional<Set<FoodPreference>> getFoodPreferences() {
		return Optional.ofNullable(foodPreferences);
	}

	public Optional<UUID> getPictureId() {
		return Optional.ofNullable(pictureId);
	}

	public Optional<ChangeUserPasswordRequest> getPassword() {
		return Optional.ofNullable(password);
	}
}
