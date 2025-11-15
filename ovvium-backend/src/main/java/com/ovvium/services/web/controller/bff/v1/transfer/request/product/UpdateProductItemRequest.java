package com.ovvium.services.web.controller.bff.v1.transfer.request.product;

import com.ovvium.services.model.product.ProductType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class UpdateProductItemRequest extends UpdateProductRequest {

	private ProductType type;

	public Optional<ProductType> getType() {
		return Optional.ofNullable(type);
	}

}
