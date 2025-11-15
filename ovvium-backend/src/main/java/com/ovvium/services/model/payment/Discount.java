package com.ovvium.services.model.payment;


import com.ovvium.services.model.common.MoneyAmount;
import com.ovvium.services.util.ovvium.base.Preconditions;
import com.ovvium.services.util.ovvium.domain.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.Embedded;
import javax.persistence.Entity;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

/**
 * TODO Not MVP. But it can exist a Discount when paying orders of the Bill by User.
 */
@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
@Accessors(chain = true)
public class Discount extends BaseEntity {

    @Embedded
    private MoneyAmount amount;

    public Discount(MoneyAmount amount){
        this.amount = Preconditions.checkNotNull(amount, "Discount amount cannot be null");
    }

}
