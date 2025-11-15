package com.ovvium.services.web.controller.bff.v1.transfer.request.customer;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import java.net.URI;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class UpdateCustomerRequest {

	private String name;
	private String description;
	private String cif;
	private String address;
	private Set<String> phones = Collections.emptySet();
	private String pciSplitUserId;
	private @Valid CreateCommissionConfigRequest commissionConfig;
	private UUID pictureId;
	private String latitude;
	private String longitude;
	private String timeZone;
	private URI website;
	private String invoiceNumberPrefix;

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

	public Optional<String> getName() {
		return Optional.ofNullable(name);
	}

	public Optional<String> getDescription() {
		return Optional.ofNullable(description);
	}

	public Optional<String> getCif() {
		return Optional.ofNullable(cif);
	}

	public Optional<String> getAddress() {
		return Optional.ofNullable(address);
	}

	public Optional<String> getPciSplitUserId() {
		return Optional.ofNullable(pciSplitUserId);
	}

	public Optional<CreateCommissionConfigRequest> getCommissionConfig() {
		return Optional.ofNullable(commissionConfig);
	}

	public Optional<String> getInvoiceNumberPrefix() {
		return Optional.ofNullable(invoiceNumberPrefix);
	}
}
