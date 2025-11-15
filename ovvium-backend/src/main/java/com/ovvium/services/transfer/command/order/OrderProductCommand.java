package com.ovvium.services.transfer.command.order;

import com.ovvium.services.model.bill.ServiceTime;
import com.ovvium.services.model.product.Product;
import com.ovvium.services.model.user.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public record OrderProductCommand(
		User user,
		Product product,
		ServiceTime serviceTime,
		String notes,
		List<OrderGroupChoicesCommand> groupChoices,
		List<UUID> selectedOptions
) {

	public Optional<String> getNotes() {
		return Optional.ofNullable(notes);
	}

	public Optional<ServiceTime> getServiceTime() {
		return Optional.ofNullable(serviceTime);
	}

	public Optional<User> getUser() {
		return Optional.ofNullable(user);
	}
}
