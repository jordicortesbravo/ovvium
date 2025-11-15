package com.ovvium.services.web.controller.bff.v1.transfer.request.customer;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class CreateCustomerRequest {

	@NotNull
	private UUID userId;
	@NotEmpty
	private String name;
	@NotEmpty
	private String description;
	@NotEmpty
	private String cif;
	@NotEmpty
	private String address;
	@NotEmpty
	private Set<String> phones;
	@NotEmpty
	private String pciSplitUserId;
	@NotNull
	private @Valid CreateCommissionConfigRequest commissionConfig;

	@NotEmpty
	private String invoiceNumberPrefix;


	private UUID pictureId;
	private String latitude;
	private String longitude;
	private String timeZone;
	private URI website;

	public Optional<UUID> getPictureId() {
		return Optional.ofNullable(pictureId);
	}

	public Optional<String> getLatitude() {
		return Optional.ofNullable(latitude);
	}

	public Optional<String> getLongitude() {
		return Optional.ofNullable(longitude);
	}

	public Optional<URI> getWebsite() {
		return Optional.ofNullable(website);
	}

	public Optional<String> getTimeZone() {
		return Optional.ofNullable(timeZone);
	}
}
