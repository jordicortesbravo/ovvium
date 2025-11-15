package com.ovvium.services.model.payment;

import com.ovvium.services.model.customer.Customer;
import com.ovvium.services.util.ovvium.base.Preconditions;
import com.ovvium.services.util.ovvium.domain.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import java.time.LocalDate;
import java.util.UUID;

import static com.ovvium.services.model.payment.InvoiceDateStatus.CLOSED;
import static com.ovvium.services.model.payment.InvoiceDateStatus.OPEN;
import static javax.persistence.EnumType.STRING;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
@Accessors(chain = true)
public class InvoiceDate extends BaseEntity {

	@Type(type = "pg-uuid")
	private UUID customerId;

	private LocalDate date;

	@Enumerated(STRING)
	private InvoiceDateStatus status = OPEN;

	public InvoiceDate(Customer customer, LocalDate date) {
		this.customerId = Preconditions.checkNotNull(customer, "Customer cannot be null").getId();
		this.date = Preconditions.checkNotNull(date, "Date cannot be null");
	}

	public InvoiceDate open() {
		this.status = OPEN;
		return this;
	}

	public InvoiceDate close() {
		this.status = CLOSED;
		return this;
	}

}
