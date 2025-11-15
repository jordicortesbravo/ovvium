package com.ovvium.services.web.controller.bff.v1.transfer.request.product;

import com.ovvium.services.model.common.MoneyAmount;
import com.ovvium.services.transfer.request.common.MultiLangStringRequest;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class ProductItemOptionRequest {

	@NotNull
	private MultiLangStringRequest title;

	@NotNull
	private MoneyAmount basePrice;

	@NotNull
	private Double tax;
}
