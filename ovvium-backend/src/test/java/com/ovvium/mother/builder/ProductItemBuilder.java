package com.ovvium.mother.builder;

import com.ovvium.mother.model.CategoryMother;
import com.ovvium.mother.model.CustomerMother;
import com.ovvium.mother.model.PictureMother;
import com.ovvium.services.model.bill.ServiceBuilderLocation;
import com.ovvium.services.model.common.MoneyAmount;
import com.ovvium.services.model.common.MultiLangString;
import com.ovvium.services.model.customer.Customer;
import com.ovvium.services.model.product.*;
import com.ovvium.services.util.util.reflection.ReflectionUtils;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.ovvium.mother.model.ProductMother.PATATAS_BRAVAS_ID;

@Setter
@Accessors(chain = true)
public class ProductItemBuilder {

	public static final Double TAX = 0.1;

	private UUID id = PATATAS_BRAVAS_ID;
	private Customer customer = CustomerMother.getElBulliCustomer();
	private String name = "Patatas Bravas";
	private String description = "Descripción";
	private Category category = CategoryMother.getEntrantesCategory(customer);
	private MoneyAmount price = MoneyAmount.ofDouble(4.5f);
	private int order = 0;
	private double tax = TAX;
	private Picture coverPicture = PictureMother.getCoverPicture();
	private Picture userPicture = PictureMother.getUserPicture();
	private ProductType type = ProductType.FOOD;
	private List<ProductOptionGroup> options = Collections.emptyList();

	public ProductItem build() {
		ReflectionUtils.set(category, "customer", this.customer);
		ProductItem product = new ProductItem(
				customer, //
				new MultiLangString(name),
				category, //
				type, //
				ServiceBuilderLocation.KITCHEN, //
				price,
				tax,
				order);
		product.setDescription(new MultiLangString(description));
		Optional.ofNullable(coverPicture).ifPresent(product::setCoverPicture);
		Optional.ofNullable(userPicture).ifPresent(product::addPicture);
		Optional.ofNullable(options).ifPresent(product::setOptions);
		ReflectionUtils.set(product, "id", id);
		return product;
	}

}
