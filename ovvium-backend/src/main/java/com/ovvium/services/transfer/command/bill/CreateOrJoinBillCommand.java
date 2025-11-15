package com.ovvium.services.transfer.command.bill;

import com.ovvium.services.model.customer.Customer;
import com.ovvium.services.model.customer.Location;
import com.ovvium.services.model.user.User;

import java.util.List;
import java.util.Optional;

public record CreateOrJoinBillCommand(
        User user,
        Customer customer,
        List<Location> locations
) {

    public Optional<User> getUser() {
        return Optional.ofNullable(user);
    }
}
