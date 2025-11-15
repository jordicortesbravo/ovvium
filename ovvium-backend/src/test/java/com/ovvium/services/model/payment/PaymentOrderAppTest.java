package com.ovvium.services.model.payment;

import com.ovvium.mother.model.BillMother;
import com.ovvium.mother.model.UserMother;
import com.ovvium.services.model.bill.Bill;
import com.ovvium.services.model.bill.Order;
import com.ovvium.services.model.common.MoneyAmount;
import com.ovvium.services.model.user.PciProvider;
import com.ovvium.services.model.user.User;
import com.ovvium.services.util.util.basic.Utils;
import org.junit.Test;

import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;
import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.*;

public class PaymentOrderAppTest {

    @Test
    public void given_payment_bill_and_user_when_new_paymentOrder_from_app_then_should_not_throw_exception() {
        User user = UserMother.getUserWithCardData();
        Bill bill = BillMother.getOpenedBillWithOpenOrder();

        assertThatCode(
                () -> new PaymentOrderApp(bill, user, PciProvider.PAYCOMET)
        ).doesNotThrowAnyException();
    }

    @Test
    public void given_payment_order_with_orders_when_get_total_amount_then_should_return_amount_correctly() {
        User user = UserMother.getUserWithCardData();
        Bill bill = BillMother.getOpenedBillWithMultipleOrders();
        BigDecimal amount = ZERO;

        PaymentOrder paymentOrder = new PaymentOrderApp(bill, user, PciProvider.PAYCOMET);
        for (Order order : bill.getOrders()) {
            paymentOrder.addOrders(singleton(order));
            amount = amount.add(order.getPrice().getAmount());
        }
        //when
        MoneyAmount totalAmount = paymentOrder.getTotalAmount();

        assertThat(totalAmount.getAmount()).isEqualTo(amount);
    }

    @Test
    public void given_payment_order_with_tip_and_orders_when_get_total_amount_then_should_return_amount_correctly() {
        User user = UserMother.getUserWithCardData();
        Bill bill = BillMother.getOpenedBillWithMultipleOrders();
        BigDecimal amount = ZERO;

        PaymentOrder paymentOrder = new PaymentOrderApp(bill, user, PciProvider.PAYCOMET);
        for (Order order : bill.getOrders()) {
            paymentOrder.addOrders(singleton(order));
            amount = amount.add(order.getPrice().getAmount());
        }
        Tip tip = new Tip(MoneyAmount.ofDouble(4));
        paymentOrder.setTip(tip);
        amount = amount.add(tip.getAmount().getAmount());

        //when
        MoneyAmount totalAmount = paymentOrder.getTotalAmount();

        assertThat(totalAmount.getAmount()).isEqualTo(amount);
    }

    @Test
    public void given_payment_bill_with_order_paid_when_add_same_order_should_throw_exception() {
        User user = UserMother.getUserWithCardData();
        Bill bill = BillMother.getOpenedBillWithOpenOrder();
        Order order = Utils.first(bill.getOrders());

        PaymentOrder paymentOrder = new PaymentOrderApp(bill, user, PciProvider.PAYCOMET);
        paymentOrder.addOrders(singleton(order));
        order.markAsPaid();

        // when, then
        assertThatThrownBy(
                () -> paymentOrder.addOrders(singleton(order))
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Order is already paid.");
    }

}