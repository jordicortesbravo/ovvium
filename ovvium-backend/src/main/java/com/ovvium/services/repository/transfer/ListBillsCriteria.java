package com.ovvium.services.repository.transfer;

import com.ovvium.services.model.bill.BillStatus;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public final class ListBillsCriteria {

    private final BillStatus billStatus;

    public Optional<BillStatus> getBillStatus() {
        return Optional.ofNullable(billStatus);
    }
}
