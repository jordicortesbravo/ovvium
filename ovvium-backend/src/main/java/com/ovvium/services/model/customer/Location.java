package com.ovvium.services.model.customer;

import com.ovvium.services.util.ovvium.domain.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

import static com.ovvium.services.util.ovvium.base.Preconditions.*;
import static com.ovvium.services.util.ovvium.domain.entity.TypeConstants.PG_UUID;
import static java.lang.String.format;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Accessors(chain = true)
@NoArgsConstructor(access = PROTECTED)
public class Location extends BaseEntity {

	private final static int MAX_DESCRIPTION_SIZE = 20;

	@Type(type = PG_UUID)
	private UUID customerId;

	@Embedded
	@AttributeOverride(name = "value", column = @Column(name = "tag_id"))
	private TagId tagId;

	@Embedded
	@AttributeOverride(name = "value", column = @Column(name = "serial_number"))
	private SerialNumber serialNumber;

	private int position;

	private String description;

	@ManyToOne
	@JoinColumn
	private Zone zone;

	@Setter
	private boolean advancePayment;

	public Location(Customer customer, Zone zone, TagId tagId, SerialNumber serialNumber, int position) {
		this.customerId = checkNotNull(customer, "Customer can´t be null").getId();
		setPosition(position);
		setTagId(tagId);
		setSerialNumber(serialNumber);
		setZone(zone);
		setDescription("Mesa " + position);
		customer.getLocations().add(this);
	}

	public void setPosition(int position) {
		this.position = check(position, position >= 0, "Position can't be negative");
	}

	public void setTagId(TagId tagId) {
		this.tagId = checkNotNull(tagId, "Tag Id cannot be null");
	}

	public void setSerialNumber(SerialNumber serialNumber) {
		this.serialNumber = checkNotNull(serialNumber, "SerialNumber cannot be null");
	}

	public Location setDescription(String description) {
		checkNotBlank(description, "Description can´t be empty");
		this.description = checkMaxCharacters(description, MAX_DESCRIPTION_SIZE,
				format("Description max length is %d", MAX_DESCRIPTION_SIZE));
		return this;
	}

	public Location setZone(Zone zone) {
		checkNotNull(zone, "Zone can't be null");
		this.zone = check(zone,
				zone.getCustomerId().equals(this.customerId),
				"Zone must be from the same customer, but was " + zone.getCustomerId());
		return this;
	}

}
