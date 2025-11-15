package com.ovvium.mother.model;

import com.ovvium.mother.builder.ProductItemBuilder;
import com.ovvium.services.model.bill.ServiceBuilderLocation;
import com.ovvium.services.model.bill.ServiceTime;
import com.ovvium.services.model.common.MoneyAmount;
import com.ovvium.services.model.common.MultiLangString;
import com.ovvium.services.model.customer.Customer;
import com.ovvium.services.model.product.*;
import com.ovvium.services.util.util.reflection.ReflectionUtils;
import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.ovvium.mother.builder.ProductItemBuilder.TAX;
import static java.util.Collections.singletonList;

@UtilityClass
public class ProductMother {

	public static final UUID PATATAS_BRAVAS_ID = UUID.fromString("5db5cf1d-cba4-4a2f-ba11-8a7dc7db46ad");
	public static final UUID CERVEZA_ID = UUID.fromString("541cec2e-0ab1-4df9-a6af-6d9f0e826a8b");
	public static final UUID MENU_PRODUCT_ID = UUID.fromString("28927561-01eb-4d23-b937-b2175dd39e72");
	public static final UUID COFFEE_PRODUCT_ID = UUID.fromString("5b5ac434-1fb0-4a80-bedc-560b8fd4cd9e");
	public static final UUID IRISH_COFFEE_OPTION_ID = UUID.fromString("5c9042cf-9dba-4375-b627-c9440c5ed270");

	public static ProductItem getPatatasBravasProduct() {
		return new ProductItemBuilder().build();
	}

	public static ProductItem getCervezaProduct() {
		Customer elBulliCustomer = CustomerMother.getElBulliCustomer();
		final ProductItem product = new ProductItemBuilder()
				.setCustomer(elBulliCustomer)
				.setName("Cerveza")
				.setCategory(CategoryMother.getBebidasCategory(elBulliCustomer))
				.setType(ProductType.DRINK)
				.setPrice(MoneyAmount.ofDouble(1.4f))
				.build();
		ReflectionUtils.set(product, "id", CERVEZA_ID);
		return product;
	}

	public static ProductGroup getMenuDiarioProduct() {
		Customer elBulliCustomer = CustomerMother.getElBulliCustomer();
		ProductGroup productGroup = new ProductGroup(
				elBulliCustomer, //
				new MultiLangString("Cerveza"),
				CategoryMother.getBebidasCategory(elBulliCustomer), //
				ServiceBuilderLocation.KITCHEN, //
				MoneyAmount.ofDouble(10f),
				TAX,
				0,
				singletonList(new ProductGroupEntry(ServiceTime.SOONER, Collections.singleton(getCervezaProduct())))
		);
		productGroup.setDescription(new MultiLangString("Cerveza Fresquita"));
		ReflectionUtils.set(productGroup, "id", MENU_PRODUCT_ID);
		return productGroup;
	}

	public static ProductItem getCoffeeProduct() {
		Customer elBulliCustomer = CustomerMother.getElBulliCustomer();
		final ProductItem product = new ProductItemBuilder()
				.setCustomer(elBulliCustomer)
				.setName("Café")
				.setCategory(CategoryMother.getBebidasCategory(elBulliCustomer))
				.setType(ProductType.DRINK)
				.setPrice(MoneyAmount.ofDouble(1.4f))
				.setOptions(getCoffeeOptions())
				.build();
		ReflectionUtils.set(product, "id", COFFEE_PRODUCT_ID);
		return product;
	}

	public static List<ProductOptionGroup> getCoffeeOptions() {
		return Arrays.asList(
				new ProductOptionGroup(mls("Tipo de café"),
						ProductOptionGroup.ProductOptionType.SINGLE,
						Arrays.asList(new ProductOption(mls("Sólo"), MoneyAmount.ofDouble(0), 0.1),
								new ProductOption(mls("Cortado"), MoneyAmount.ofDouble(0), 0.1),
								new ProductOption(mls("Americano"), MoneyAmount.ofDouble(0), 0.1),
								getIrishCoffeeOption(),
								new ProductOption(mls("Carajillo"), MoneyAmount.ofDouble(0.75d), 0.1)
						), true),

				new ProductOptionGroup(mls("Dulce"),
						ProductOptionGroup.ProductOptionType.MULTI,
						Arrays.asList(
								new ProductOption(mls("Napolitana"), MoneyAmount.ofDouble(1.25d), 0.1),
								new ProductOption(mls("Cookie"), MoneyAmount.ofDouble(0.75d), 0.1),
								new ProductOption(mls("Pastas francesas"), MoneyAmount.ofDouble(2.15d), 0.1),
								new ProductOption(mls("Tarta de chocolate"), MoneyAmount.ofDouble(2.5d), 0.1)
						), true)
		);
	}

	public static ProductOption getIrishCoffeeOption() {
		ProductOption irishCoffeeOption = new ProductOption(mls("Irlandés"), MoneyAmount.ofDouble(1.50d), 0.1);
		ReflectionUtils.set(irishCoffeeOption, "id", IRISH_COFFEE_OPTION_ID);
		return irishCoffeeOption;
	}

	private static MultiLangString mls(String defaultValue) {
		return new MultiLangString(defaultValue);
	}
}
