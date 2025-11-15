package com.ovvium.services.web.controller.bff.v1.transfer.request.product;


import com.ovvium.services.model.product.ProductOptionGroup.ProductOptionType;
import com.ovvium.services.transfer.request.common.MultiLangStringRequest;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class ProductItemOptionGroupRequest {

	@NotBlank
	private MultiLangStringRequest title;

	@NotNull
	private ProductOptionType type;

	@NotNull
	private Boolean required;

	@NotEmpty
	private List<ProductItemOptionRequest> choices;
}
