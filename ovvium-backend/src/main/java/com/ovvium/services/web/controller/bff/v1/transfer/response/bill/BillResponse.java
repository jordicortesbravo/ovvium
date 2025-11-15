package com.ovvium.services.web.controller.bff.v1.transfer.response.bill;

import com.ovvium.services.model.bill.Bill;
import com.ovvium.services.web.controller.bff.v1.transfer.response.common.ResourceIdResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.customer.CustomerResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.customer.EmployeeResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.location.LocationResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.order.OrderResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.user.UserResponse;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public final class BillResponse extends ResourceIdResponse {

    private final CustomerResponse customer;
    private final List<LocationResponse> locations;
    private final String status;
    private final EmployeeResponse employee;
    private final List<UserResponse> members;
    private final List<OrderResponse> orders;
    private final boolean hasJoinedLocations;

    // FIXME remove and use updateDate instead
    private final long updated;
    private final String creationDate;
    private final String updateDate;

    public BillResponse(Bill bill, CustomerResponse customer, List<UserResponse> members, List<OrderResponse> orders) {
        super(bill);
        this.customer = customer;
        this.locations = bill.getLocations().stream().map(LocationResponse::new).collect(Collectors.toList());
        this.status = bill.getBillStatus().name();
        this.employee = bill.getEmployee().map(EmployeeResponse::new).orElse(null);
        this.members = members;
        this.hasJoinedLocations = bill.hasJoinedLocations();
        this.orders = orders;
        this.updated = bill.getUpdated().toEpochMilli();
        this.creationDate = bill.getCreated().toString();
        this.updateDate = bill.getUpdated().toString();
    }
}
