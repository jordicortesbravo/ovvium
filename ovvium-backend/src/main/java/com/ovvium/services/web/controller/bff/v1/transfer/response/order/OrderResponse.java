package com.ovvium.services.web.controller.bff.v1.transfer.response.order;

import com.ovvium.services.model.bill.IssueStatus;
import com.ovvium.services.model.bill.Order;
import com.ovvium.services.model.bill.PaymentStatus;
import com.ovvium.services.transfer.response.order.SelectedProductOptionResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.common.ResourceIdResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.product.ProductResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.user.UserResponse;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public final class OrderResponse extends ResourceIdResponse {

    private final UserResponse user;
    private final ProductResponse product;
    private final IssueStatus issueStatus;
    private final PaymentStatus paymentStatus;
    private final double basePrice;
    private final double price;
    private final double tax;
    private final String serviceTime;
    private final long orderTime;
    private final String notes;
    private final List<OrderGroupChoiceResponse> groupChoices;
    private final List<SelectedProductOptionResponse> selectedOptions;

    public OrderResponse(Order order, UserResponse userResponse, ProductResponse productResponse, List<OrderGroupChoiceResponse> groupChoices) {
        super(order);
        this.issueStatus = order.getIssueStatus();
        this.paymentStatus = order.getPaymentStatus();
        this.user = userResponse;
        this.product = productResponse;
        this.price = order.getPrice().asDouble();
        this.basePrice = order.getBasePrice().asDouble();
        this.tax = order.getTax();
        this.serviceTime = order.getServiceTime().name();
        this.orderTime = order.getCreated().toEpochMilli();
        this.notes = order.getNotes().orElse(null);
        this.groupChoices = groupChoices;
        this.selectedOptions = order.getSelectedOptions().stream()
                .map(SelectedProductOptionResponse::new)
                .collect(Collectors.toList());

    }

}
