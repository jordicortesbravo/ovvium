package com.ovvium.services.model.payment;

import com.ovvium.services.model.common.MoneyAmount;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

import static com.ovvium.services.util.ovvium.base.Preconditions.checkNotBlank;
import static com.ovvium.services.util.ovvium.base.Preconditions.checkNotNull;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Embeddable
@NoArgsConstructor(access = PROTECTED)
public class ProviderTransactionDetails {

	// Transaction ID, used to do a refund using the pci provider
	private String transactionId;

	@Embedded
	private MoneyAmount amount;

	public ProviderTransactionDetails(String transactionId, MoneyAmount amount) {
		this.transactionId = checkNotBlank(transactionId, "Transaction id cannot be blank");
		this.amount = checkNotNull(amount, "Amount cannot be blank cannot be null");
	}
}
