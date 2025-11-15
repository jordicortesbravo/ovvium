package com.ovvium.services.model.product;

import com.ovvium.mother.builder.ProductItemBuilder;
import com.ovvium.mother.model.CategoryMother;
import com.ovvium.mother.model.CustomerMother;
import com.ovvium.services.model.bill.ServiceBuilderLocation;
import com.ovvium.services.model.common.MoneyAmount;
import com.ovvium.services.model.common.MultiLangString;
import com.ovvium.services.model.customer.Customer;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ProductItemTest extends ProductTest {


    @Test
    public void given_customer_and_category_for_other_customer_when_create_product_then_should_throw_exception() {
        Category category = CategoryMother.getBebidasCategory(CustomerMother.getElBulliCustomer());
        Customer customer = CustomerMother.getCanRocaCustomer();

        assertThatThrownBy(() -> new ProductItem(
                customer,
                new MultiLangString("Name"),
                category,
                ProductType.DRINK,
                ServiceBuilderLocation.KITCHEN,
                MoneyAmount.ofDouble(2),
                1,
                0
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Category must be from the same customer, but was " + category.getCustomer().getId());
    }


    @Test
    public void given_new_product_when_create_product_then_should_create_hidden_product() {
        Customer customer = CustomerMother.getCanRocaCustomer();
        Category category = CategoryMother.getBebidasCategory(customer);

        Product product = new ProductItem(
                customer,
                new MultiLangString("Name"),
                category,
                ProductType.DRINK,
                ServiceBuilderLocation.KITCHEN,
                MoneyAmount.ofDouble(2),
                1,
                0
        );

        assertThat(product.isHidden()).isTrue();
    }

    @Override
    protected Product getProductOfPriceAndTax(MoneyAmount price, double tax) {
        return new ProductItemBuilder()
                .setPrice(price)
                .setTax(tax)
                .build();
    }

    @Override
    protected Product getProductOfOrder(int order) {
        return new ProductItemBuilder()
                .setOrder(order)
                .build();
    }
}