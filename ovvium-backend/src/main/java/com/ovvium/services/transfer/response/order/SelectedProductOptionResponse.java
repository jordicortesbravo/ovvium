package com.ovvium.services.transfer.response.order;

import com.ovvium.services.model.bill.SelectedProductOption;
import com.ovvium.services.transfer.response.common.MultiLangStringResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.common.MoneyAmountResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.common.ResourceIdResponse;
import lombok.Getter;

@Getter
public final class SelectedProductOptionResponse extends ResourceIdResponse {

    private final MultiLangStringResponse title;
    private final MoneyAmountResponse basePrice;
    private final Double tax;

    public SelectedProductOptionResponse(SelectedProductOption selectedProductOption) {
        super(selectedProductOption);
        this.title = new MultiLangStringResponse(selectedProductOption.getTitle());
        this.basePrice = new MoneyAmountResponse(selectedProductOption.getBasePrice());
        this.tax = selectedProductOption.getTax();
    }
}
