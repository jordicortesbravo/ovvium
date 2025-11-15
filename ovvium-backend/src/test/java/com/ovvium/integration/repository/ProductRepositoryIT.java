package com.ovvium.integration.repository;

import com.ovvium.services.model.common.MoneyAmount;
import com.ovvium.services.model.common.MultiLangString;
import com.ovvium.services.model.customer.Customer;
import com.ovvium.services.model.product.Category;
import com.ovvium.services.model.product.Product;
import com.ovvium.services.model.product.ProductItem;
import com.ovvium.services.repository.CategoryRepository;
import com.ovvium.services.repository.CustomerRepository;
import com.ovvium.services.repository.ProductRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ovvium.integration.DbDataConstants.CUSTOMER_1_ID;
import static com.ovvium.services.model.bill.ServiceBuilderLocation.KITCHEN;
import static com.ovvium.services.model.product.ProductType.FOOD;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class ProductRepositoryIT extends AbstractRepositoryIT {

	// SUT
	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private CategoryRepository categoryRepository;


	@Test
	public void given_saved_categories_and_products_when_list_products_then_return_ordered_by_category_and_product_order() {
		Customer customer = customerRepository.getOrFail(CUSTOMER_1_ID);

		Category category1 = new Category(customer, new MultiLangString("Postres"), 2);
		Category category2 = new Category(customer, new MultiLangString("Entrantes"), 1);
		Stream.of(category1, category2).forEach(categoryRepository::save);

		ProductItem product1 = new ProductItem(customer, new MultiLangString("Olivas"), category2, FOOD, KITCHEN, MoneyAmount.ofDouble(2), 0, 3);
		ProductItem product2 = new ProductItem(customer, new MultiLangString("Huevos"), category2, FOOD, KITCHEN, MoneyAmount.ofDouble(2), 0, 2);
		ProductItem product3 = new ProductItem(customer, new MultiLangString("Helado"), category1, FOOD, KITCHEN, MoneyAmount.ofDouble(2), 0, 1);
		Stream.of(product1, product2, product3).forEach(productRepository::save);

		List<Product> products = productRepository.list(customer.getId())
				.stream()
				.filter(p -> asList(product1, product2, product3).contains(p))
				.collect(Collectors.toList());

		List<Integer> orders = products.stream().map(Product::getOrder).collect(Collectors.toList());
		assertThat(orders).containsExactly(2, 3, 1);
	}

}
