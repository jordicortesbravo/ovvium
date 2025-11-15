package com.ovvium.mother.builder;

import com.ovvium.mother.model.BillMother;
import com.ovvium.mother.model.UserMother;
import com.ovvium.services.model.bill.Bill;
import com.ovvium.services.model.bill.Order;
import com.ovvium.services.model.common.MoneyAmount;
import com.ovvium.services.model.payment.PaymentOrderApp;
import com.ovvium.services.model.payment.ProviderTransactionDetails;
import com.ovvium.services.model.payment.Tip;
import com.ovvium.services.model.user.PciProvider;
import com.ovvium.services.model.user.User;
import com.ovvium.services.util.util.reflection.ReflectionUtils;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Setter
@Accessors(chain = true)
public class PaymentOrderAppCardBuilder {

	private Bill bill = BillMother.getOpenedBillWithOpenOrder();
	private User user = UserMother.getUserJorge();
	private MoneyAmount tipAmount;
	private ProviderTransactionDetails purchaseTransactionDetails;
	private Set<Order> orders = Collections.emptySet();
	private MoneyAmount splitAmount;
	private UUID pciTransactionId = UUID.randomUUID();

	public PaymentOrderApp build() {
		PaymentOrderApp paymentOrder = new PaymentOrderApp(bill, user, PciProvider.PAYCOMET);
		Optional.ofNullable(tipAmount).map(Tip::new).ifPresent(paymentOrder::setTip);
		Optional.ofNullable(splitAmount).ifPresent(paymentOrder::setSplitCustomerAmount);
		Optional.ofNullable(purchaseTransactionDetails).ifPresent(paymentOrder::setPurchaseTransactionDetails);
		paymentOrder.addOrders(orders);
		ReflectionUtils.set(paymentOrder, "pciTransactionId", pciTransactionId);
		return paymentOrder;
	}

}
