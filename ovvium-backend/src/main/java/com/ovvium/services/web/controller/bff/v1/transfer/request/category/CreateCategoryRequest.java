package com.ovvium.services.web.controller.bff.v1.transfer.request.category;

import com.ovvium.services.transfer.request.common.MultiLangStringRequest;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class CreateCategoryRequest {

	@NotNull
	private MultiLangStringRequest name;

}
