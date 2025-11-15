package com.ovvium.services.repository;

import com.ovvium.services.model.bill.Bill;
import com.ovvium.services.model.customer.Customer;
import com.ovvium.services.model.customer.Location;
import com.ovvium.services.model.payment.InvoiceDate;
import com.ovvium.services.repository.transfer.ListBillsCriteria;
import com.ovvium.services.util.jpa.core.DefaultRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BillRepository extends DefaultRepository<Bill, UUID> {

	Optional<Bill> getOpenByLocation(UUID locationId);

	Optional<Bill> getLastBillOfUser(UUID userId);

	List<Bill> listBills(Customer customer, ListBillsCriteria criteria);

	boolean existsOpenByLocations(List<Location> locations);

	boolean existsOpenByInvoiceDate(InvoiceDate invoiceDate);
}
