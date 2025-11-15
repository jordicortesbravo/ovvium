package com.ovvium.services.model.customer;

import com.google.common.collect.ImmutableSet;
import com.ovvium.services.util.ovvium.domain.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang.StringUtils;

import javax.persistence.Entity;
import java.util.Set;

import static com.ovvium.services.app.constant.Roles.CUSTOMERS_USER;
import static com.ovvium.services.util.ovvium.base.Preconditions.*;
import static lombok.AccessLevel.PROTECTED;
import static org.springframework.util.StringUtils.trimAllWhitespace;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
@Accessors(chain = true)
public class Employee extends BaseEntity {

	private String name;
	private String accessCode;

	public Employee(Customer customer, String name, String accessCode) {
		setName(name);
		this.accessCode = checkValidAccessCode(accessCode);
		checkNotNull(customer, "Customer can't be null").getEmployees().add(this);
	}

	public Employee setName(String name) {
		this.name = checkNotBlank(name, "Name cannot be blank");
		return this;
	}

	// We hardcode this for now, if we need more roles in the future we will add a new column
	public Set<String> getRoles() {
		return ImmutableSet.of(CUSTOMERS_USER);
	}

	private String checkValidAccessCode(String accessCode) {
		boolean isCorrectSize = trimAllWhitespace(accessCode).length() == 4;
		boolean isNumeric = StringUtils.isNumeric(accessCode);
		return check(accessCode, isCorrectSize && isNumeric, "Access code must be a 4-length numeric code.");
	}
}
