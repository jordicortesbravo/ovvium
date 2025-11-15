package com.ovvium.services.web.controller.bff.v1.transfer.response.category;

import com.ovvium.services.model.product.Category;
import com.ovvium.services.transfer.response.common.MultiLangStringResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.common.ResourceIdResponse;
import lombok.Getter;

@Getter
public final class CategoryResponse extends ResourceIdResponse {

	private final int order;
	private final MultiLangStringResponse name;

	public CategoryResponse(Category category) {
		super(category);
		this.order = category.getOrder();
		this.name = new MultiLangStringResponse(category.getName());
	}
}
