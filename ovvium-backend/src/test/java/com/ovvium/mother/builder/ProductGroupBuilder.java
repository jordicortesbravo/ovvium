package com.ovvium.mother.builder;

import com.ovvium.mother.model.CategoryMother;
import com.ovvium.mother.model.CustomerMother;
import com.ovvium.mother.model.PictureMother;
import com.ovvium.mother.model.ProductMother;
import com.ovvium.services.model.bill.ServiceBuilderLocation;
import com.ovvium.services.model.bill.ServiceTime;
import com.ovvium.services.model.common.MoneyAmount;
import com.ovvium.services.model.common.MultiLangString;
import com.ovvium.services.model.customer.Customer;
import com.ovvium.services.model.product.Category;
import com.ovvium.services.model.product.Picture;
import com.ovvium.services.model.product.ProductGroup;
import com.ovvium.services.model.product.ProductGroupEntry;
import com.ovvium.services.util.util.reflection.ReflectionUtils;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;

import static com.ovvium.mother.builder.ProductItemBuilder.TAX;
import static com.ovvium.mother.model.ProductMother.MENU_PRODUCT_ID;
import static java.util.Collections.singleton;

@Setter
@Accessors(chain = true)
public class ProductGroupBuilder {

	private UUID id = MENU_PRODUCT_ID;
	private Customer customer = CustomerMother.getElBulliCustomer();
	private String name = "Patatas Bravas";
	private String description = "Descripción";
	private Category category = CategoryMother.getEntrantesCategory(customer);
	private MoneyAmount price = MoneyAmount.ofDouble(10f);
	private int order = 0;
	private double tax = TAX;
	private Picture coverPicture = PictureMother.getCoverPicture();
	private Picture userPicture = PictureMother.getUserPicture();
	private LocalTime startTime;
	private LocalTime endTime;
	private Set<DayOfWeek> daysOfWeek;
	private List<ProductGroupEntry> entries = Collections.singletonList(new ProductGroupEntry(ServiceTime.SOONER, singleton(ProductMother.getPatatasBravasProduct())));

	public ProductGroup build() {
		ReflectionUtils.set(category, "customer", this.customer);
		ProductGroup product = new ProductGroup(
				customer, //
				new MultiLangString(name),
				category, //
				ServiceBuilderLocation.KITCHEN, //
				price,
				tax,
				order,
				entries);
		ReflectionUtils.set(product, "id", id);
		product.setDescription(new MultiLangString(description));
		product.setStartTime(startTime);
		product.setEndTime(endTime);
		product.setDaysOfWeek(daysOfWeek);

		Optional.ofNullable(coverPicture).ifPresent(product::setCoverPicture);
		Optional.ofNullable(userPicture).ifPresent(product::addPicture);
		return product;
	}

}
