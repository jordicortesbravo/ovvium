package com.ovvium.services.web.controller.bff.v1.transfer.request.product;

import com.ovvium.services.model.bill.ServiceBuilderLocation;
import com.ovvium.services.model.user.Allergen;
import com.ovvium.services.transfer.request.common.MultiLangStringRequest;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class UpdateProductRequest {

	private Double basePrice;
	private Double tax;
	private ServiceBuilderLocation serviceBuilderLocation;
	private UUID categoryId;
	private UUID coverPictureId;
	private Integer order;
	private Boolean hidden;
	private Boolean recommended;
	private Set<Allergen> allergens;
	private MultiLangStringRequest name;
	private MultiLangStringRequest description;
	private List<ProductItemOptionGroupRequest> options;

	public Optional<Double> getBasePrice() {
		return Optional.ofNullable(basePrice);
	}

	public Optional<Double> getTax() {
		return Optional.ofNullable(tax);
	}

	public Optional<ServiceBuilderLocation> getServiceBuilderLocation() {
		return Optional.ofNullable(serviceBuilderLocation);
	}

	public Optional<UUID> getCategoryId() {
		return Optional.ofNullable(categoryId);
	}

	public Optional<Integer> getOrder() {
		return Optional.ofNullable(order);
	}

	public Optional<Boolean> getHidden() {
		return Optional.ofNullable(hidden);
	}

	public Optional<Boolean> getRecommended() {
		return Optional.ofNullable(recommended);
	}

	public Optional<Set<Allergen>> getAllergens() {
		return Optional.ofNullable(allergens);
	}

	public Optional<MultiLangStringRequest> getName() {
		return Optional.ofNullable(name);
	}

	public Optional<MultiLangStringRequest> getDescription() {
		return Optional.ofNullable(description);
	}

	public Optional<UUID> getCoverPictureId() {
		return Optional.ofNullable(coverPictureId);
	}

	public Optional<List<ProductItemOptionGroupRequest>> getOptions() { return Optional.ofNullable(options); }
}
