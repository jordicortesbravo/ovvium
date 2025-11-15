package com.ovvium.services.web.controller.bff.v1.transfer.response.order;

import com.ovvium.services.model.bill.IssueStatus;
import com.ovvium.services.model.bill.OrderGroupChoice;
import com.ovvium.services.model.bill.ServiceTime;
import com.ovvium.services.web.controller.bff.v1.transfer.response.common.ResourceIdResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.product.ProductResponse;
import lombok.Getter;

@Getter
public final class OrderGroupChoiceResponse extends ResourceIdResponse {

	private final ProductResponse product;
	private final IssueStatus issueStatus;
	private final ServiceTime serviceTime;
	private final long orderTime;
	private final String notes;

	public OrderGroupChoiceResponse(OrderGroupChoice choice, ProductResponse productResponse) {
		super(choice);
		this.issueStatus = choice.getIssueStatus();
		this.product = productResponse;
		this.serviceTime = choice.getServiceTime();
		this.orderTime = choice.getCreated().toEpochMilli();
		this.notes = choice.getNotes().orElse(null);
	}

}
