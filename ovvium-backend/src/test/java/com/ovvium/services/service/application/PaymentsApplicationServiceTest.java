package com.ovvium.services.service.application;

import com.ovvium.mother.builder.LocationBuilder;
import com.ovvium.mother.model.CustomerMother;
import com.ovvium.mother.model.UserMother;
import com.ovvium.services.model.customer.Location;
import com.ovvium.services.service.CustomerService;
import com.ovvium.services.service.PaymentService;
import com.ovvium.services.service.UserService;
import com.ovvium.services.web.controller.bff.v1.transfer.request.payment.AdvancePaymentAppCardRequest;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PaymentsApplicationServiceTest {

    private PaymentsApplicationService service;

    private CustomerService customerService;

    @Before
    public void setUp() {
        customerService = mock(CustomerService.class);
        service = new PaymentsApplicationService(
            mock(PaymentService.class),
                customerService,
                mock(UserService.class)
        );
    }

    @Test
    public void given_app_card_advance_payment_for_joined_locations_with_no_advance_payment_when_create_app_card_payment_then_shold_throw_exception() {
        var customer = CustomerMother.getElBulliCustomer();
        var locations = List.of(
                new LocationBuilder().setAdvancePayment(false).setCustomer(customer).build(),
                new LocationBuilder().setAdvancePayment(false).setCustomer(customer).withRandomId().build());
        when(customerService.getCustomer(customer.getId())).thenReturn(customer);
        var request = new AdvancePaymentAppCardRequest()
                .setCustomerId(customer.getId())
                .setLocationIds(locations.stream().map(Location::getId).collect(Collectors.toUnmodifiableSet()))
                .setPciDetailsId(UserMother.USER_PCI_DETAILS_ID);

        // when
        assertThatThrownBy(() -> service.payAndOrder(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("Advance Payment for these Locations not allowed:");
    }

}