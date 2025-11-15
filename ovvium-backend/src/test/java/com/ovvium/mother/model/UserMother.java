package com.ovvium.mother.model;

import com.google.common.collect.Sets;
import com.ovvium.services.model.user.User;
import com.ovvium.services.model.user.UserPciDetails;
import com.ovvium.services.util.util.reflection.ReflectionUtils;
import lombok.experimental.UtilityClass;

import java.util.UUID;

import static com.ovvium.services.util.ovvium.string.OvviumStringUtils.randomPassword;
import static java.util.Collections.singleton;

@UtilityClass
public class UserMother {

	public static UUID USER_JORGE_ID = UUID.fromString("13ad0627-859d-4fb0-9d80-1501f7270eb3");
	public static UUID USER_JORDI_ID = UUID.fromString("2faa5758-f5a5-42c1-892b-0fc94562ea5c");
	public static UUID USER_CARD_DATA_ID = UUID.fromString("a5bac9fe-d04b-43b8-a6d5-b9dfd843fd90");
	public static UUID USER_F_ADRIA_ID = UUID.fromString("fa089b4f-f38d-43f3-b3c2-72c5955e99bb");
	public static UUID USER_REMOVED_ID = UUID.fromString("34949ad5-6ee4-4ba1-a147-8c5a9d2c87be");
	public static UUID USER_PCI_DETAILS_ID = UUID.fromString("418a6573-84e7-4c5d-af55-f596fd89a8b9");
	public static final String CUSTOMER_USER_FADRIA_EMAIL = "fadria@dummy.com";
	public static final String USER_CARD_DATA_EMAIL = "bill.gates@dummy.com";
	public static final String USER_PCI_ID = "0001";
	public static final String USER_PCI_TOKEN = "usertoken";

	// Create User INSTANCES here as it is slow to create a user because of bcrypt
	private static final User USER_JORGE = createUserJorge();
	private static final User USER_JORDI = createUserJordi();
	private static final User USER_CARD_DATA = createUserWithCardData();
	private static final User CUSTOMER_FERRAN_ADRIA = createCustomerUserFAdria();
	private static final User USER_REMOVED = createRemovedUser();

	public static User getUserJorge() {
		return USER_JORGE;
	}

	public static User getUserJordi() {
		return USER_JORDI;
	}

	public static User getUserWithCardData() {
		return USER_CARD_DATA;
	}

	public static User getCustomerUserFAdria() {
		return CUSTOMER_FERRAN_ADRIA;
	}

	public static User getRemovedUser() {
		return USER_REMOVED;
	}

	private static User createUserJorge() {
		User user = User.basicUser("Jorge", "jorge@dummy.com", randomPassword());
		ReflectionUtils.set(user, "id", USER_JORGE_ID);
		ReflectionUtils.set(user, "enabled", true);
		return user;
	}

	private static User createUserJordi() {
		User user = User.basicUser("Jordi", "jordi@dummy.com", randomPassword());
		ReflectionUtils.set(user, "id", USER_JORDI_ID);
		ReflectionUtils.set(user, "enabled", true);
		return user;
	}

	private static User createUserWithCardData() {
		User user = User.basicUser("Bill Gates", USER_CARD_DATA_EMAIL, randomPassword());
		ReflectionUtils.set(user, "id", USER_CARD_DATA_ID);
		ReflectionUtils.set(user, "enabled", true);
		UserPciDetails userPciDetails = user.addUserPciDetail(USER_PCI_ID, USER_PCI_TOKEN);
		ReflectionUtils.set(userPciDetails, "id", USER_PCI_DETAILS_ID);
		// since we changed the ID, we need to re-create the hashset because of the new ID Hash
		ReflectionUtils.set(user, "pciDetails", Sets.newLinkedHashSet(singleton(userPciDetails)));
		return user;
	}

	private static User createCustomerUserFAdria() {
		User user = User.adminCustomerUser("Ferran Adria", CUSTOMER_USER_FADRIA_EMAIL, randomPassword());
		ReflectionUtils.set(user, "id", USER_F_ADRIA_ID);
		ReflectionUtils.set(user, "enabled", true);
		return user;
	}

	private static User createRemovedUser() {
		User user = User.basicUser("User", CUSTOMER_USER_FADRIA_EMAIL, randomPassword());
		ReflectionUtils.set(user, "id", USER_F_ADRIA_ID);
		user.anonymize();
		return user;
	}

}
