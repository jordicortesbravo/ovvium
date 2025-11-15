package com.ovvium.services.model.bill;

import com.ovvium.mother.builder.BillBuilder;
import com.ovvium.mother.builder.OrderBuilder;
import com.ovvium.mother.model.BillMother;
import com.ovvium.services.util.util.basic.Utils;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.ovvium.services.model.bill.BillStatus.CLOSED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class BillTest {

    @Test
    public void given_bill_with_order_and_not_existing_order_id_when_get_orders_by_id_then_throw_exception() {
        Bill bill = BillMother.getOpenedBillWithOpenOrder();

        UUID orderId = UUID.randomUUID();

        assertThatThrownBy(
                () -> bill.getOrders(Collections.singleton(orderId))
        ).isInstanceOf(IllegalArgumentException.class)
        .hasMessage(String.format("Order Id %s not found for bill %s", orderId, bill.getId()));
    }


    @Test
    public void given_bill_with_order_and_existing_order_id_when_get_orders_by_id_then_return_order() {
        Bill bill = BillMother.getOpenedBillWithOpenOrder();
        Order order = Utils.first(bill.getOrders());

        Set<Order> orders = bill.getOrders(Collections.singleton(order.getId()));

        assertThat(orders).containsOnly(order);
    }

    @Test
    public void given_bill_with_order_when_close_then_should_mark_bill_as_closed() {
        Bill bill = BillMother.getOpenedBillWithOpenOrder();

        bill.close();

        assertThat(bill.getBillStatus().equals(CLOSED));
    }

    @Test
    public void given_bill_with_pending_order_when_is_all_paid_then_return_false() {
        Bill bill = new BillBuilder()
                .setOrders(List.of(new OrderBuilder()
                        .setPaymentStatus(PaymentStatus.PENDING)
                )).build();

        assertThat(bill.isAllPaid()).isFalse();
    }
}