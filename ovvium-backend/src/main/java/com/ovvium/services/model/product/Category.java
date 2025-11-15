package com.ovvium.services.model.product;

import com.ovvium.services.model.common.MultiLangString;
import com.ovvium.services.model.customer.Customer;
import com.ovvium.services.util.ovvium.domain.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Comparator;
import java.util.Objects;

import static com.ovvium.services.util.ovvium.base.Preconditions.check;
import static com.ovvium.services.util.ovvium.base.Preconditions.checkNotNull;
import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
@Accessors(chain = true)
public class Category extends BaseEntity implements Comparable<Category> {

	@ManyToOne(fetch = LAZY)
	private Customer customer;

	private int order;

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "defaultValue", column = @Column(name = "name_default_value")),
			@AttributeOverride(name = "translations", column = @Column(name = "name_translations"))
	})
	private MultiLangString name;

	public Category(Customer customer, MultiLangString name, int order) {
		this.customer = checkNotNull(customer, "Customer can't be null");
		setOrder(order);
		setName(name);
	}

	public Category setName(MultiLangString name) {
		this.name = checkNotNull(name, "Name cannot be null");
		return this;
	}

	public Category setOrder(int order) {
		this.order = check(order, order >= 0, "Order cannot be negative");
		return this;
	}

	@Override
	public int compareTo(Category other) {
		return Objects.compare(this, checkNotNull(other, "Category can't be null"), Comparator.comparing(Category::getOrder));
	}

}
