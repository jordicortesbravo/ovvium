package com.ovvium.services.service.application;

import com.ovvium.services.model.exception.ResourceNotFoundException;
import com.ovvium.services.security.JwtUtil;
import com.ovvium.services.service.BillService;
import com.ovvium.services.service.factory.BillServiceCommandFactory;
import com.ovvium.services.transfer.command.bill.CreateOrJoinBillCommand;
import com.ovvium.services.web.controller.bff.v1.transfer.request.bill.CreateOrJoinBillRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.response.common.ResourceIdResponse;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.ovvium.services.util.ovvium.base.Preconditions.check;

@Service
@Transactional
@RequiredArgsConstructor
public class BillApplicationService {

    private final BillServiceCommandFactory commandFactory;
    private final BillService service;

    public ResourceIdResponse createOrJoinToBill(CreateOrJoinBillRequest request) {
        CreateOrJoinBillCommand command = commandFactory.ofCreateOrJoinBill(request);
        return new ResourceIdResponse(service.createOrJoin(command));
    }

    public ResourceIdResponse createOrJoinCurrentUserToBill(CreateOrJoinBillRequest request) {
        val currentUserId = JwtUtil.getAuthenticatedUserOrFail().getId();
        CreateOrJoinBillCommand command = commandFactory.ofCreateOrJoinBill(request.setUserId(currentUserId));
        return new ResourceIdResponse(service.createOrJoin(command));
    }

    public void closeBill(UUID customerId, UUID billId) {
        var bill = service.getBill(billId);
        check(bill.getCustomerId().equals(customerId), new ResourceNotFoundException("Bill " + bill + " is not from Customer " + customerId));
        service.closeBill(bill);
    }

}
