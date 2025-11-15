package com.ovvium.services.model.payment;

import com.ovvium.services.model.bill.Bill;
import com.ovvium.services.model.common.MoneyAmount;
import com.ovvium.services.model.user.PciProvider;
import com.ovvium.services.model.user.User;
import com.ovvium.services.util.ovvium.base.Preconditions;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Optional;
import java.util.UUID;

import static com.ovvium.services.model.payment.PaymentOrderStatus.*;
import static com.ovvium.services.util.ovvium.base.Preconditions.checkNotNull;
import static com.ovvium.services.util.ovvium.domain.entity.TypeConstants.PG_UUID;
import static javax.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;


@Getter
@Entity
@DiscriminatorValue("APP")
@NoArgsConstructor(access = PROTECTED)
@Accessors(chain = true)
public class PaymentOrderApp extends PaymentOrder {

	@ManyToOne
	private User payer;

	@Setter
	@ManyToOne
	private Discount discount;

	// Internal ID used to uniquely identify the transaction with the PCI provider
	@Type(type = PG_UUID)
	private final UUID pciTransactionId = UUID.randomUUID();

	@Setter
	@Enumerated(STRING)
	private PciProvider provider;

	@Setter
	@Enumerated(STRING)
	private PaymentOrderStatus status = PENDING;

	@Setter
	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "amount", column = @Column(name = "split_customer_amount")),
			@AttributeOverride(name = "currency", column = @Column(name = "split_customer_currency"))
	})
	private MoneyAmount splitCustomerAmount;

	@Setter
	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "transactionId", column = @Column(name = "purchase_details_transaction_id")), //
			@AttributeOverride(name = "amount.amount", column = @Column(name = "purchase_details_amount")),
			@AttributeOverride(name = "amount.currency", column = @Column(name = "purchase_details_currency"))
	})
	private ProviderTransactionDetails purchaseTransactionDetails;

	@Setter
	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "transactionId", column = @Column(name = "split_details_transaction_id")), //
			@AttributeOverride(name = "amount.amount", column = @Column(name = "split_details_amount")),
			@AttributeOverride(name = "amount.currency", column = @Column(name = "split_details_currency"))
	})
	private ProviderTransactionDetails splitTransactionDetails;


	public PaymentOrderApp(Bill bill, User user, PciProvider provider) {
		super(bill, PaymentType.APP_CARD);
		this.payer = Preconditions.checkNotNull(user, "User cannot be null");
		this.provider = checkNotNull(provider, "PciProvider can't be null");
	}

	public Optional<Discount> getDiscount() {
		return Optional.ofNullable(discount);
	}

	public Optional<ProviderTransactionDetails> getPurchaseTransactionDetails() {
		return Optional.ofNullable(purchaseTransactionDetails);
	}

	public Optional<ProviderTransactionDetails> getSplitTransactionDetails() {
		return Optional.ofNullable(splitTransactionDetails);
	}

	@Override
	public MoneyAmount getTotalAmount() {
		MoneyAmount total = super.getTotalAmount();
		if (getDiscount().isPresent()) {
			total = total.subtract(getDiscount().get().getAmount());
		}
		return total;
	}

	public PaymentOrderApp confirmAsPaid() {
		this.status = CONFIRMED;
		return this;
	}

	public PaymentOrderApp cancelPayment() {
		this.status = CANCELLED;
		return this;
	}

	public boolean isPendingStatus(){
		return this.status == PENDING;
	}

}
