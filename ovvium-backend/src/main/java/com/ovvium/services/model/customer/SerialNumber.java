package com.ovvium.services.model.customer;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

import static com.ovvium.services.util.ovvium.base.Preconditions.check;
import static com.ovvium.services.util.ovvium.base.Preconditions.checkNotBlank;
import static lombok.AccessLevel.PROTECTED;
import static org.apache.commons.lang.StringUtils.isNumeric;


@Embeddable
@NoArgsConstructor(access = PROTECTED)
public final class SerialNumber {

	@Getter
	private String value;

	public SerialNumber(String serialNumber) {
		checkNotBlank(serialNumber, "SerialNumber cannot be blank");
		this.value = check(serialNumber, isNumeric(serialNumber), "This string is not a correct serial number");
	}

}
