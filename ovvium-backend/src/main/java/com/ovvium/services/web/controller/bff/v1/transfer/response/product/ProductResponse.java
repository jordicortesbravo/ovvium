package com.ovvium.services.web.controller.bff.v1.transfer.response.product;

import com.ovvium.services.model.bill.ServiceBuilderLocation;
import com.ovvium.services.model.product.Product;
import com.ovvium.services.model.product.ProductType;
import com.ovvium.services.model.rating.AverageRating;
import com.ovvium.services.model.user.Allergen;
import com.ovvium.services.transfer.response.common.MultiLangStringResponse;
import com.ovvium.services.transfer.response.product.ProductOptionGroupResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.common.ResourceIdResponse;
import lombok.Getter;
import lombok.val;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public abstract class ProductResponse extends ResourceIdResponse {

	private final String productType;
	private final UUID customerId;
	private final UUID categoryId;
	private final int order;
	private final double basePrice;
	private final double price;
	private final double tax;
	private final ProductType type;
	private final ServiceBuilderLocation serviceBuilderLocation;
	private final Set<Allergen> allergens;
	private final boolean hidden;
	private final boolean recommended;

	// Estos datos solo tienen sentido en las apps. ¿Quizás haya que distinguir
	// entre app y PoS? Inicialmente no, para simplificar!
	// FIXME : Jorge: esto seria un pattern BFF BFA
	// https://samnewman.io/patterns/architectural/bff/
	private final long nComments;
	private final float rate;
	private final int rateAsInt;

	private final MultiLangStringResponse categoryName;
	private final MultiLangStringResponse name;
	private final MultiLangStringResponse description;
	private final List<ProductOptionGroupResponse> optionGroups;


	public ProductResponse(Product product, AverageRating rating, String productType) {
		super(product);
		this.productType = productType;
		this.customerId = product.getCustomer().getId();
		this.order = product.getOrder();
		this.basePrice = product.getBasePrice().asDouble();
		this.tax = product.getTax();
		this.price = product.getPrice().asDouble();
		this.type = product.getType();
		this.serviceBuilderLocation = product.getServiceBuilderLocation();
		this.hidden = product.isHidden();
		this.recommended = product.isRecommended();

		this.categoryId = product.getCategory().getId();
		this.categoryName = new MultiLangStringResponse(product.getCategory().getName());

		this.allergens = product.getAllergens();
		this.name = new MultiLangStringResponse(product.getName());
		this.description = product.getDescription()
				.map(MultiLangStringResponse::new)
				.orElse(null);
		this.optionGroups = product.getOptionGroups().stream()
				.map(ProductOptionGroupResponse::new)
				.collect(Collectors.toList());

		val optRating = Optional.ofNullable(rating);
		this.rate = optRating.map(r -> r.getAverage().getAmount()).orElse(0f);
		this.rateAsInt = optRating.map(AverageRating::getAverageAsInt).orElse(0);
		this.nComments = optRating.map(AverageRating::getTotalRatings).orElse(0L);
	}

}
