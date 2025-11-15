package com.ovvium.mother.model;

import com.ovvium.services.model.common.MultiLangString;
import com.ovvium.services.model.customer.Customer;
import com.ovvium.services.model.product.Category;
import lombok.experimental.UtilityClass;


@UtilityClass
public class CategoryMother {

	public static final String ENTRANTES_CATEGORY = "ENTRANTES";
	public static final String BEBIDAS = "BEBIDAS";
	public static final String PACKS = "PACKS";

	public static Category getEntrantesCategory(Customer customer) {
		return new Category(customer, new MultiLangString(ENTRANTES_CATEGORY), 0);
	}

	public static Category getBebidasCategory(Customer customer) {
		return new Category(customer, new MultiLangString(BEBIDAS), 0);
	}

	public static Category getPacksCategory(Customer customer) {
		return new Category(customer, new MultiLangString(PACKS), 0);
	}

}
