package com.ovvium.services.repository.impl;

import com.mysema.query.types.expr.BooleanExpression;
import com.ovvium.services.model.bill.Bill;
import com.ovvium.services.model.bill.QBill;
import com.ovvium.services.model.customer.Customer;
import com.ovvium.services.model.customer.Location;
import com.ovvium.services.model.payment.InvoiceDate;
import com.ovvium.services.repository.BillRepository;
import com.ovvium.services.repository.transfer.ListBillsCriteria;
import com.ovvium.services.util.jpa.core.JpaDefaultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.ovvium.services.model.bill.BillStatus.OPEN;

@Repository
@RequiredArgsConstructor
public class BillRepositoryImpl extends JpaDefaultRepository<Bill, UUID> implements BillRepository {

	private static final QBill qBill = QBill.bill;

	@Override
	public Optional<Bill> getOpenByLocation(UUID locationId) {
		return get(qBill.locations.any().id.eq(locationId).and(qBill.billStatus.eq(OPEN)));
	}

	@Override
	public Optional<Bill> getLastBillOfUser(UUID userId) {
		return first(qBill.members.any().id.eq(userId), qBill.created.desc());
	}

	@Override
	public List<Bill> listBills(Customer customer, ListBillsCriteria criteria) {
		BooleanExpression predicate = qBill.customerId.eq(customer.getId());
		if (criteria.getBillStatus().isPresent()) {
			predicate = predicate.and(qBill.billStatus.eq(criteria.getBillStatus().get()));
		}
		return list(predicate, qBill.created.asc());
	}

	@Override
	public boolean existsOpenByLocations(List<Location> locations) {
		return exists(qBill.locations.any().in(locations).and(qBill.billStatus.eq(OPEN)));
	}

	@Override
	public boolean existsOpenByInvoiceDate(InvoiceDate invoiceDate) {
		return exists(qBill.invoiceDateId.eq(invoiceDate.getId()).and(qBill.billStatus.eq(OPEN)));
	}
}
