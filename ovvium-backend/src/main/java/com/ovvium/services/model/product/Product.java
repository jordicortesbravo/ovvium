package com.ovvium.services.model.product;

import com.ovvium.services.model.bill.ServiceBuilderLocation;
import com.ovvium.services.model.common.MoneyAmount;
import com.ovvium.services.model.common.MultiLangString;
import com.ovvium.services.model.customer.Customer;
import com.ovvium.services.model.exception.OvviumDomainException;
import com.ovvium.services.model.user.Allergen;
import com.ovvium.services.model.user.converter.AllergenSetConverter;
import com.ovvium.services.util.ovvium.base.Preconditions;
import com.ovvium.services.util.ovvium.domain.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.*;

import static com.ovvium.services.model.exception.ErrorCode.PRODUCT_PRICE_TOO_LOW;
import static com.ovvium.services.util.ovvium.base.Preconditions.*;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.InheritanceType.SINGLE_TABLE;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@Accessors(chain = true)
@Inheritance(strategy = SINGLE_TABLE)
@DiscriminatorColumn(name = "product_type")
@NoArgsConstructor(access = PROTECTED)
public abstract class Product extends BaseEntity implements Comparable<Product> {

	@ManyToOne
	private Customer customer;

	private int order;

	@ManyToOne
	private Category category;

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "defaultValue", column = @Column(name = "name_default_value")),
			@AttributeOverride(name = "translations", column = @Column(name = "name_translations"))
	})
	private MultiLangString name;

	@Setter
	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "defaultValue", column = @Column(name = "description_default_value")),
			@AttributeOverride(name = "translations", column = @Column(name = "description_translations"))
	})
	private MultiLangString description;

	@Setter
	@ManyToOne
	private Picture coverPicture;

	@ManyToMany
	@OrderBy("created ASC")
	private List<Picture> pictures = new ArrayList<>();

	@Enumerated(STRING)
	private ProductType type;

	@Enumerated(STRING)
	private ServiceBuilderLocation serviceBuilderLocation;

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "amount", column = @Column(name = "base_price_amount")),
			@AttributeOverride(name = "currency", column = @Column(name = "base_price_currency"))
	})
	private MoneyAmount basePrice;

	private double tax;

	@Setter
	@Convert(converter = AllergenSetConverter.class)
	private Set<Allergen> allergens = EnumSet.noneOf(Allergen.class);


	@JoinColumn(name = "product_id")
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ProductOptionGroup> optionGroups = new ArrayList<>();

	private boolean hidden = true;

	private boolean recommended;

	protected Product(Customer customer,
					  MultiLangString name,
					  Category category,
					  ProductType type,
					  ServiceBuilderLocation serviceBuilderLocation,
					  MoneyAmount basePrice,
					  double tax,
					  int order) {
		this.customer = checkNotNull(customer, "Customer can't be null");
		setName(name);
		setServiceBuilderLocation(serviceBuilderLocation);
		setType(type);
		setBasePrice(basePrice);
		setTax(tax);
		setCategory(category);
		setOrder(order);
	}

	public Product setType(ProductType type) {
		this.type = checkNotNull(type, "ProductType can't be null");
		return this;
	}

	public Product setServiceBuilderLocation(ServiceBuilderLocation sbLocation) {
		this.serviceBuilderLocation = checkNotNull(sbLocation, "ServiceBuilderLocation can't be null");
		return this;
	}

	public Product setCategory(Category category) {
		checkNotNull(category, "Category can't be null");
		this.category = check(category,
				category.getCustomer().equals(this.customer),
				"Category must be from the same customer, but was " + category.getCustomer().getId());
		return this;
	}

	public Product setName(MultiLangString name) {
		this.name = checkNotNull(name, "Product name cannot be null");
		return this;
	}

	public void setBasePrice(MoneyAmount basePrice) {
		checkNotNull(basePrice, "BasePrice can't be null");
		checkPriceHigherThanCommission(basePrice, this.tax);
		this.basePrice = basePrice;
	}

	public void setTax(double tax) {
		checkRange(tax, 0d, 1d, "Tax should be in (0,1) range, was " + tax);
		checkPriceHigherThanCommission(this.basePrice, tax);
		this.tax = tax;
	}

	public void setOrder(int order) {
		this.order = check(order, order >= 0, "Order cannot be negative, was " + order);
	}

	public MoneyAmount getPrice() {
		return getTotalPrice(basePrice, tax);
	}

	public Product addPicture(Picture picture) {
		this.pictures.add(checkNotNull(picture, "Picture can't be null"));
		return this;
	}

	public Optional<Picture> getCoverPicture() {
		return Optional.ofNullable(coverPicture);
	}

	public Product publish() {
		this.hidden = false;
		return this;
	}

	public Product hide() {
		this.hidden = true;
		return this;
	}

	public Product recommend() {
		this.recommended = true;
		return this;
	}

	public Product unrecommend() {
		this.recommended = false;
		return this;
	}

	public Optional<MultiLangString> getDescription() {
		return Optional.ofNullable(description);
	}

	public Product setOptions(List<ProductOptionGroup> optionGroups) {
		this.optionGroups = optionGroups;
		optionGroups.forEach(optionGroup -> Preconditions.checkNotEmpty(optionGroup.getOptions(), "Product option can't be null"));
		return this;
	}

	@SuppressWarnings("unchecked")
	public <T extends Product> T as(Class<T> clazz) {
		if (clazz.isAssignableFrom(this.getClass())) {
			return (T) this;
		}
		throw new IllegalStateException("Class is not from type " + clazz.getSimpleName());
	}

	@Override
	public int compareTo(Product other) {
		return Objects.compare(this, checkNotNull(other, "Product can't be null"), Comparator.comparing(Product::getOrder));
	}

	private void checkPriceHigherThanCommission(MoneyAmount basePrice, double tax) {
		check(getTotalPrice(basePrice, tax).isGreaterOrEqualThan(customer.getCommissionConfig().getMaxMinimumCommission()),
				new OvviumDomainException(PRODUCT_PRICE_TOO_LOW));
	}

	private MoneyAmount getTotalPrice(MoneyAmount basePrice, double tax) {
		return basePrice.add(basePrice.multiply(tax));
	}
}
