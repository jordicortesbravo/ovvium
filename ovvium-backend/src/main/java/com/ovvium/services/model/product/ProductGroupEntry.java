package com.ovvium.services.model.product;

import com.ovvium.services.model.bill.ServiceTime;
import com.ovvium.services.util.ovvium.domain.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Set;

import static com.ovvium.services.util.ovvium.base.Preconditions.checkNotEmpty;
import static com.ovvium.services.util.ovvium.base.Preconditions.checkNotNull;
import static javax.persistence.EnumType.STRING;

@Getter
@Entity
@Accessors(chain = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductGroupEntry extends BaseEntity {

	@Enumerated(STRING)
	private ServiceTime serviceTime;

	@ManyToMany
	private Set<ProductItem> products = new HashSet<>();

	public ProductGroupEntry(ServiceTime serviceTime, Set<ProductItem> products) {
		this.serviceTime = checkNotNull(serviceTime, "ServiceTime can't be null");
		setProducts(products);
	}

	public ProductGroupEntry setProducts(Set<ProductItem> products) {
		checkNotEmpty(products, "Products cannot be empty");
		this.products.clear();
		this.products.addAll(products);
		return this;
	}

}
