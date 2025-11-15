package com.ovvium.services.transfer.response.product;

import com.ovvium.services.model.product.ProductOption;
import com.ovvium.services.transfer.response.common.MultiLangStringResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.common.MoneyAmountResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.common.ResourceIdResponse;
import lombok.Getter;

@Getter
public class ProductOptionResponse extends ResourceIdResponse {

    private MultiLangStringResponse title;
    private MoneyAmountResponse basePrice;
    private Double tax;

    public ProductOptionResponse(ProductOption productOption) {
        super(productOption);
        this.title = new MultiLangStringResponse(productOption.getTitle());
        this.basePrice = new MoneyAmountResponse(productOption.getBasePrice());
        this.tax = productOption.getTax();
    }
}
