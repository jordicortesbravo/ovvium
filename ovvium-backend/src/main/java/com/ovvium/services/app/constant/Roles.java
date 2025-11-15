package com.ovvium.services.app.constant;

import static com.google.common.collect.Sets.newHashSet;

import java.util.Set;
import java.util.stream.Collectors;

import com.ovvium.services.util.util.reflection.ReflectionUtils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Roles {

	// Spring WebSecurity uses this as prefix by default
	private static final String SPRING_ROLE_PREFIX = "ROLE_";

	public static final String CUSTOMERS_ADMIN = SPRING_ROLE_PREFIX + "CUSTOMERS_ADMIN";
	public static final String CUSTOMERS_USER = SPRING_ROLE_PREFIX + "CUSTOMERS_USER";
	public static final String OVVIUM_ADMIN = SPRING_ROLE_PREFIX + "OVVIUM_ADMIN";

	public static final String USERS = SPRING_ROLE_PREFIX + "USERS";

	public static final Set<String> ALL_ROLES = newHashSet(ReflectionUtils.getStringConstants(Roles.class));

	public static Set<String> getCustomerRoles() {
		return filterBy("CUSTOMERS");
	}
	
	public static Set<String> getUserRoles() {
		return filterBy("USERS");
	}

	private static Set<String> filterBy(String pattern) {
		return ReflectionUtils.getStringConstants(Roles.class).stream().filter(it -> it.contains(pattern))
				.collect(Collectors.toSet());
	}

}
