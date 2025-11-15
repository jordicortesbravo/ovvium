package com.ovvium.services.model.product;

import com.ovvium.services.model.bill.ServiceBuilderLocation;
import com.ovvium.services.model.bill.ServiceTime;
import com.ovvium.services.model.common.MoneyAmount;
import com.ovvium.services.model.common.MultiLangString;
import com.ovvium.services.model.customer.Customer;
import com.ovvium.services.model.exception.OvviumDomainException;
import com.ovvium.services.model.exception.ResourceNotFoundException;
import com.ovvium.services.model.product.converter.DaysOfWeekSetConverter;
import com.ovvium.services.util.ovvium.domain.entity.BaseEntity;
import com.ovvium.services.util.util.container.Pair;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.val;
import org.apache.commons.collections.CollectionUtils;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.util.*;

import static com.ovvium.services.model.exception.ErrorCode.PRODUCT_GROUP_TIMES_NOT_CORRECT;
import static com.ovvium.services.util.ovvium.base.Preconditions.check;
import static com.ovvium.services.util.ovvium.base.Preconditions.checkNotEmpty;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Getter
@Entity
@DiscriminatorValue("PRODUCT_GROUP")
@Accessors(chain = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductGroup extends Product {

	private static final LocalTime DEFAULT_END_TIME = LocalTime.MAX;
	private static final LocalTime DEFAULT_START_TIME = LocalTime.MIN;
	private static final EnumSet<DayOfWeek> DEFAULT_DAYS_OF_WEEK = EnumSet.allOf(DayOfWeek.class);

	@Convert(converter = DaysOfWeekSetConverter.class)
	private Set<DayOfWeek> daysOfWeek = DEFAULT_DAYS_OF_WEEK;

	private LocalTime startTime = DEFAULT_START_TIME;

	private LocalTime endTime = DEFAULT_END_TIME;

	@JoinColumn(name = "product_group_id")
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ProductGroupEntry> entries = new ArrayList<>();

	public ProductGroup(Customer customer, MultiLangString name, Category category,
						ServiceBuilderLocation serviceBuilderLocation, MoneyAmount basePrice, double tax,
						int order, List<ProductGroupEntry> entries) {
		super(customer, name, category, ProductType.GROUP, serviceBuilderLocation, basePrice, tax, order);
		setEntries(entries);
	}

	public ProductGroup setDaysOfWeek(Set<DayOfWeek> daysOfWeek) {
		this.daysOfWeek = CollectionUtils.isEmpty(daysOfWeek) ? DEFAULT_DAYS_OF_WEEK : daysOfWeek;
		return this;
	}

	public ProductGroup setStartTime(LocalTime startTime) {
		this.startTime = startTime == null ? DEFAULT_START_TIME : startTime;
		check(this.startTime.isBefore(endTime), new OvviumDomainException(PRODUCT_GROUP_TIMES_NOT_CORRECT, "Start time cannot be after End time"));
		return this;
	}

	public ProductGroup setEndTime(LocalTime endTime) {
		this.endTime = endTime == null ? DEFAULT_END_TIME : endTime;
		check(this.endTime.isAfter(startTime), new OvviumDomainException(PRODUCT_GROUP_TIMES_NOT_CORRECT, "End time cannot be before start time"));
		return this;
	}

	public boolean isTimeRangeAvailable(Instant now) {
		val timeZone = getCustomer().getTimeZone();
		val dayOfWeek = now.atZone(timeZone).getDayOfWeek();
		if(!daysOfWeek.contains(dayOfWeek)) {
			return false;
		}
		val nowTime = LocalTime.ofInstant(now, timeZone);
		return !nowTime.isBefore(startTime) && !nowTime.isAfter(endTime);
	}

	public Map<ServiceTime, Set<ProductItem>> getProducts() {
		return entries.stream()
				.collect(toMap(ProductGroupEntry::getServiceTime, ProductGroupEntry::getProducts));
	}

	public Pair<ProductItem, ServiceTime> getProductItem(UUID productId) {
		for (val entry : getProducts().entrySet()) {
			ServiceTime serviceTime = entry.getKey();
			Set<ProductItem> products = entry.getValue();
			val productsById = products.stream()
					.collect(toMap(BaseEntity::getId, identity()));
			val product = Optional.ofNullable(productsById.get(productId));
			if(product.isPresent()) {
				return new Pair<>(product.get(), serviceTime);
			}
		}
		throw new ResourceNotFoundException("Product " + productId + " not found for this ProductGroup");
	}

	public ProductGroup setEntries(List<ProductGroupEntry> productEntries) {
		checkNotEmpty(productEntries, "Product Entries cannot be empty.");
		val entryMap = entries.stream()
				.collect(toMap(ProductGroupEntry::getServiceTime, identity()));
		val serviceEntries = new ArrayList<ServiceTime>();
		// Optimisation to avoid multiple DELETES on DB
		for (val pe : productEntries) {
			if (entryMap.containsKey(pe.getServiceTime())) {
				entryMap.get(pe.getServiceTime()).setProducts(pe.getProducts());
			} else {
				this.entries.add(pe);
			}
			serviceEntries.add(pe.getServiceTime());
		}
		val diff = CollectionUtils.subtract(entryMap.keySet(), serviceEntries);
		this.entries.removeIf(entry -> diff.contains(entry.getServiceTime()));
		return this;
	}

}
