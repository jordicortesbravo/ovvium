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

@Getter
@Entity
@NoArgsConstructor(access = PRIVATE)
@Accessors(chain = true)
public class Tip extends BaseEntity {

	@Embedded
	private MoneyAmount amount;

	public Tip(MoneyAmount tipAmount) {
		this.amount = Preconditions.checkNotNull(tipAmount, "Tip cannot be null");
	}

}
