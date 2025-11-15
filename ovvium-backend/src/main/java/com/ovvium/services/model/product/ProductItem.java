package com.ovvium.services.model.product;

import com.ovvium.services.model.bill.ServiceBuilderLocation;
import com.ovvium.services.model.common.MoneyAmount;
import com.ovvium.services.model.common.MultiLangString;
import com.ovvium.services.model.customer.Customer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Getter
@Entity
@Accessors(chain = true)
@DiscriminatorValue("PRODUCT_ITEM")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductItem extends Product {

    public ProductItem(Customer customer,
                       MultiLangString name,
                       Category category,
                       ProductType type,
                       ServiceBuilderLocation serviceBuilderLocation,
                       MoneyAmount basePrice,
                       double tax,
                       int order) {
        super(customer, name, category, type, serviceBuilderLocation, basePrice, tax, order);
    }
}
