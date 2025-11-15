package com.ovvium.services.model.product;

import com.ovvium.services.model.common.MoneyAmount;
import com.ovvium.services.model.common.MultiLangString;
import com.ovvium.services.util.ovvium.base.Preconditions;
import com.ovvium.services.util.ovvium.domain.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;

import static com.ovvium.services.util.ovvium.base.Preconditions.checkNotNull;
import static com.ovvium.services.util.ovvium.base.Preconditions.checkRange;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
@Accessors(chain = true)
public class ProductOption extends BaseEntity {

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

    public ProductOption(MultiLangString title, MoneyAmount basePrice, double tax) {
        setTitle(title);
        setBasePrice(basePrice);
        setTax(tax);
    }

    public ProductOption setTitle(MultiLangString title) {
        this.title = Preconditions.checkNotNull(title, "title can't be blank");
        return this;
    }

    public ProductOption setBasePrice(MoneyAmount basePrice) {
        checkNotNull(basePrice, "BasePrice can't be null");
        this.basePrice = basePrice;
        return this;
    }

    public ProductOption setTax(double tax) {
        checkRange(tax, 0d, 1d, "Tax should be in (0,1) range, was " + tax);
        this.tax = tax;
        return this;
    }

    public MoneyAmount getPrice() {
        return basePrice.add(basePrice.multiply(tax));
    }

    public boolean isFree() {
        return MoneyAmount.ZERO.equals(basePrice);
    }
}
