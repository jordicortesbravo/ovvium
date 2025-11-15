package com.ovvium.services.web.controller.bff.v1.transfer.request.category;

import com.ovvium.services.transfer.request.common.MultiLangStringRequest;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Optional;

@Data
@Accessors(chain = true)
public class UpdateCategoryRequest {

	private MultiLangStringRequest name;

	private Integer order;

	public Optional<Integer> getOrder() {
		return Optional.ofNullable(order);
	}

	public Optional<MultiLangStringRequest> getName() {
		return Optional.ofNullable(name);
	}
}
