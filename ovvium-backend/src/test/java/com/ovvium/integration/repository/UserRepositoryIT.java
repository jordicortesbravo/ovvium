package com.ovvium.integration.repository;

import com.ovvium.integration.DbDataConstants;
import com.ovvium.mother.model.UserMother;
import com.ovvium.services.model.user.User;
import com.ovvium.services.repository.UserRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.EnumSet;

import static com.ovvium.services.model.user.Allergen.DAIRY_PRODUCTS;
import static com.ovvium.services.model.user.Allergen.EGGS;
import static org.assertj.core.api.Assertions.assertThat;

public class UserRepositoryIT extends AbstractRepositoryIT {

	// SUT
	@Autowired
	private UserRepository repository;

	@Test
	public void given_db_saved_user_when_get_user_from_db_then_should_get_user_correctly() {
		User savedUser = repository.getOrFail(DbDataConstants.USER_1_ID);

		assertThat(savedUser.getName()).isEqualTo("Jorge Padilla");
	}

	@Test
	public void given_new_user_with_allergen_when_save_user_check_allergens_are_correclty_stored() {
		final User user = UserMother.getUserJorge();
		user.setAllergens(EnumSet.of(EGGS, DAIRY_PRODUCTS));

		repository.save(user);

		User savedUser = repository.getOrFail(user.getId());
		assertThat(savedUser.getAllergens())
				.containsExactlyInAnyOrder(EGGS, DAIRY_PRODUCTS);
	}

}
