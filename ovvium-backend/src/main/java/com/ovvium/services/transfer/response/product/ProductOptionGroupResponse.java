package com.ovvium.services.transfer.response.product;

import com.ovvium.services.model.product.ProductOptionGroup;
import com.ovvium.services.transfer.response.common.MultiLangStringResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.common.ResourceIdResponse;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ProductOptionGroupResponse extends ResourceIdResponse {

    private MultiLangStringResponse title;
    private String type;
    private List<ProductOptionResponse> options;
    private boolean required;

    public ProductOptionGroupResponse(ProductOptionGroup optionGroup) {
        super(optionGroup);
        this.title = new MultiLangStringResponse(optionGroup.getTitle());
        this.type = optionGroup.getType().name();
        this.options = optionGroup.getOptions().stream().map(o -> new ProductOptionResponse(o)).collect(Collectors.toList());
        this.required = optionGroup.isRequired();
    }
}
