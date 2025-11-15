package com.ovvium.services.model.customer;

import com.ovvium.services.util.ovvium.domain.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import java.util.UUID;

import static com.ovvium.services.util.ovvium.base.Preconditions.checkNotBlank;
import static com.ovvium.services.util.ovvium.base.Preconditions.checkNotNull;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Accessors(chain = true)
@NoArgsConstructor(access = PROTECTED)
public class Zone extends BaseEntity {

	private UUID customerId;

	private String name;

	public Zone(Customer customer, String name) {
		this.customerId = checkNotNull(customer, "Customer can´t be null").getId();
		this.name = checkNotBlank(name, "Zone's name can't be blank");
	}
}
