package com.ovvium.services.transfer.command.payment;

import com.ovvium.services.model.customer.Customer;
import com.ovvium.services.model.customer.Location;
import com.ovvium.services.model.payment.Tip;
import com.ovvium.services.model.user.User;
import com.ovvium.services.model.user.UserPciDetails;
import com.ovvium.services.web.controller.bff.v1.transfer.request.order.CreateOrderRequest;

import java.util.List;

public record AdvancePaymentAppCardCommand(
    Customer customer,
    List<Location> locations,
    User currentUser,
    UserPciDetails userPciDetails,
    List<CreateOrderRequest> orders,
    Tip tip
){
}
