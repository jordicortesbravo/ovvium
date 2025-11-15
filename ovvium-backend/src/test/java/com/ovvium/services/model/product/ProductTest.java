package com.ovvium.services.model.product;

import com.ovvium.services.model.common.MoneyAmount;
import com.ovvium.services.model.exception.ErrorCode;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public abstract class ProductTest {

    @Test
    public void given_correct_base_price_and_tax_when_getPrice_should_return_correct_price() {
        Product product = getProductOfPriceAndTax(MoneyAmount.ofDouble(2.0), 0.1);

        MoneyAmount price = product.getPrice();

        assertThat(price).isEqualTo(MoneyAmount.ofDouble(2.20));
    }

    @Test
    public void given_base_price_and_no_tax_when_getPrice_should_return_correct_price() {
        Product product = getProductOfPriceAndTax(MoneyAmount.ofDouble(2.0), 0);

        MoneyAmount price = product.getPrice();

        assertThat(price).isEqualTo(MoneyAmount.ofDouble(2));
    }

    @Test
    public void given_low_base_price_and_tax_when_creating_product_then_should_throw_exception() {
        assertThatThrownBy(() ->
                getProductOfPriceAndTax(MoneyAmount.ofDouble(0.0), 0.1)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorCode.PRODUCT_PRICE_TOO_LOW.getMessage());
    }

    @Test
    public void given_base_price_and_incorrect_tax_when_getPrice_should_throw_exception() {
        double incorrectTax = -0.1;

        assertThatThrownBy(() ->
                getProductOfPriceAndTax(MoneyAmount.ofDouble(2.0), incorrectTax)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Tax should be in (0,1) range, was " + incorrectTax);
    }

    @Test
    public void given_wrong_order__when_create_product_should_throw_exception() {
        int wrongOrder = -1;

        assertThatThrownBy(() ->
                getProductOfOrder(wrongOrder)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Order cannot be negative, was -1");
    }


    protected abstract Product getProductOfPriceAndTax(MoneyAmount price, double tax);

    protected abstract Product getProductOfOrder(int order);

}
