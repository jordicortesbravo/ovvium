package com.ovvium.services.model.bill;

import com.ovvium.services.model.common.MoneyAmount;
import com.ovvium.services.model.common.MultiLangString;
import com.ovvium.services.model.product.ProductOption;
import com.ovvium.services.util.ovvium.domain.entity.BaseEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;

import static lombok.AccessLevel.PROTECTED;

/**
 * Snapshot of ProductOption selected by an user and materialized and embedded within an order
 */
@Data
@Entity
@Accessors(chain = true)
@NoArgsConstructor(access = PROTECTED)
public class SelectedProductOption extends BaseEntity  {

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "defaultValue", column = @Column(name = "title_default_value")),
            @AttributeOverride(name = "translations", column = @Column(name = "title_translations"))
    })
    private MultiLangString title;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "base_price_amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "base_price_currency"))
    })
    private MoneyAmount basePrice;

    private Double tax;

    public SelectedProductOption(ProductOption productOption) {
        setTitle(productOption.getTitle());
        setBasePrice(productOption.getBasePrice());
        setTax(productOption.getTax());
    }

    public MoneyAmount getPrice() {
        return basePrice.add(basePrice.multiply(tax));
    }

    public boolean isFree() {
        return basePrice == null || MoneyAmount.ZERO.equals(basePrice);
    }
}
