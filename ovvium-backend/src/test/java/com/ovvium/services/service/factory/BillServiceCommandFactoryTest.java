package com.ovvium.services.service.factory;

import com.ovvium.services.repository.CustomerRepository;
import com.ovvium.services.repository.UserRepository;
import com.ovvium.services.web.controller.bff.v1.transfer.request.bill.CreateOrJoinBillRequest;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import static com.ovvium.mother.model.LocationMother.FIRST_LOCATION_ID;
import static com.ovvium.services.util.util.basic.Utils.set;
import static org.mockito.Mockito.mock;

public class BillServiceCommandFactoryTest {

    private BillServiceCommandFactory commandFactory;

    @Before
    public void setUp() {
        commandFactory = new BillServiceCommandFactory(mock(UserRepository.class), mock(CustomerRepository.class));
    }

    @Test
    public void given_null_customer_id_when_create_command_then_should_throw_exception() {
        CreateOrJoinBillRequest request = new CreateOrJoinBillRequest().setLocationIds(set(FIRST_LOCATION_ID));
        // when, then
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)//
                .isThrownBy(() -> { //
                    commandFactory.ofCreateOrJoinBill(request); //
                }).withMessageContaining("Customer id can't be null");
    }
}