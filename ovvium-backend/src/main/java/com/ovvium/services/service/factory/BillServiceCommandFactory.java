package com.ovvium.services.service.factory;

import com.ovvium.services.repository.CustomerRepository;
import com.ovvium.services.repository.UserRepository;
import com.ovvium.services.transfer.command.bill.CreateOrJoinBillCommand;
import com.ovvium.services.web.controller.bff.v1.transfer.request.bill.CreateOrJoinBillRequest;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.ovvium.services.util.ovvium.base.Preconditions.checkNotEmpty;
import static com.ovvium.services.util.ovvium.base.Preconditions.checkNotNull;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BillServiceCommandFactory {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;

    public CreateOrJoinBillCommand ofCreateOrJoinBill(CreateOrJoinBillRequest request) {
        val customer = customerRepository.getOrFail(checkNotNull(request.getCustomerId(), "Customer id can't be null"));
        val locationIds = checkNotEmpty(request.getLocationIds(), "Location ids can't be empty");
        return new CreateOrJoinBillCommand(
                request.getUserId().map(userRepository::getOrFail).orElse(null),
                customer,
                customer.getLocationsById(locationIds)
        );
    }

}
