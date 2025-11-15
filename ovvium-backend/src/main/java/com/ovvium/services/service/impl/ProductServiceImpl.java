package com.ovvium.services.service.impl;

import com.ovvium.services.model.bill.ServiceTime;
import com.ovvium.services.model.product.*;
import com.ovvium.services.repository.CategoryRepository;
import com.ovvium.services.repository.ProductRepository;
import com.ovvium.services.service.CustomerService;
import com.ovvium.services.service.ProductService;
import com.ovvium.services.transfer.command.category.CreateCategoryCommand;
import com.ovvium.services.transfer.command.category.UpdateCategoryCommand;
import com.ovvium.services.transfer.command.product.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

	private final CustomerService customerService;
	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;

	@Autowired
	private ProductService self;

	@Override
	public Category createCategory(CreateCategoryCommand command) {
		val customer = command.customer();
		val order = categoryRepository.getLastOrder(customer.getId()) + 1;
		val category = new Category(customer, command.title(), order);
		log.info("Category {} created for customer {}", category.getId(), customer.getId());
		return self.save(category);
	}

	@Override
	public Category getCategory(UUID categoryId) {
		return categoryRepository.getOrFail(categoryId);
	}

	@Override
	public Category updateCategory(UpdateCategoryCommand command) {
		val category = command.category();
		command.getTitle().ifPresent(category::setName);
		command.getOrder().ifPresent(category::setOrder);
		return self.save(category);
	}

	@Override
	public List<Category> listCategories(UUID customerId) {
		return categoryRepository.listByCustomer(customerId);
	}

	@Override
	public Product getProduct(UUID productId) {
		return productRepository.getOrFail(productId);
	}

	@Override
	public List<Product> listProducts(UUID customerId) {
		val customer = customerService.getCustomer(customerId);
		return productRepository.list(customer.getId());
	}

	@Override
	public ProductItem create(CreateProductItemCommand command) {
		val customer = command.customer();
		val product = new ProductItem(
				customer,
				command.name(),
				command.category(),
				command.productType(),
				command.serviceBuilderLocation(),
				command.basePrice(),
				command.tax(),
				productRepository.getLastOrder(customer.getId(), command.category().getId()) + 1
		);
		command.getDescription().ifPresent(product::setDescription);
		command.getCoverPicture()
				.ifPresent(product::setCoverPicture);
		product.setAllergens(command.allergens());
		command.getOptions().ifPresent(product::setOptions);
		self.save(product);
		log.info("Product Item {} created for customer {}", product.getId(), customer.getId());
		return product;
	}

	@Override
	public Product addPictureToProduct(Product product, Picture picture) {
		product.addPicture(picture);
		self.save(product);
		return product;
	}

	@Override
	public ProductGroup createGroup(CreateProductGroupCommand command) {
		val customer = command.customer();
		val productEntries = command.products().entrySet().stream()
				.filter(it -> !it.getValue().isEmpty())
				.map(this::createProductGroupEntries)
				.collect(toList());
		val product = new ProductGroup(
				customer,
				command.name(),
				command.category(),
				command.serviceBuilderLocation(),
				command.basePrice(),
				command.tax(),
				productRepository.getLastOrder(customer.getId(), command.category().getId()) + 1,
				productEntries
		);
		command.getDescription().ifPresent(product::setDescription);
		command.getCoverPicture()
				.ifPresent(product::setCoverPicture);
		product.setAllergens(command.allergens());
		product.setDaysOfWeek(command.daysOfWeek());
		command.getStartTime().ifPresent(product::setStartTime);
		command.getEndTime().ifPresent(product::setEndTime);
		command.getOptions().ifPresent(product::setOptions);
		self.save(product);
		log.info("Product Group {} created for customer {}", product.getId(), customer.getId());
		return product;
	}

	@Override
	public Product update(UpdateProductCommand command) {
		val product = command.getProduct();
		command.getName().ifPresent(product::setName);
		command.getDescription().ifPresent(product::setDescription);
		command.getBasePrice().ifPresent(product::setBasePrice);
		command.getTax().ifPresent(product::setTax);
		command.getServiceBuilderLocation()
				.ifPresent(product::setServiceBuilderLocation);
		command.getAllergens().ifPresent(product::setAllergens);
		command.getCategory()
				.filter(it -> product.getCategory().getId() != it.getId())
				.ifPresent(category -> {
					product.setCategory(category);
					int newOrder = productRepository.getLastOrder(product.getCustomer().getId(), category.getId()) + 1;
					product.setOrder(newOrder);
				});
		command.getOrder().ifPresent(product::setOrder);
		command.getHidden().ifPresent(hidden -> {
			if (hidden) product.hide();
			else product.publish();
		});
		command.getRecommended().ifPresent(recommend -> {
			if (recommend) product.recommend();
			else product.unrecommend();
		});
		command.getCoverPicture()
				.ifPresent(product::setCoverPicture);
		command.getOptions().ifPresent(product::setOptions);
		self.save(product);
		log.info("Product {} updated.", product.getId());
		return product;
	}

	@Override
	public Product updateItem(UpdateProductItemCommand command) {
		val product = update(command).as(ProductItem.class);
		command.getType().ifPresent(product::setType);
		return self.save(product);
	}

	@Override
	public Product updateGroup(UpdateProductGroupCommand command) {
		val product = update(command).as(ProductGroup.class);
		command.getDaysOfWeek().ifPresent(product::setDaysOfWeek);
		command.getStartTime().ifPresent(product::setStartTime);
		command.getEndTime().ifPresent(product::setEndTime);
		command.getProducts()
				.map(ids -> ids.entrySet().stream()
						.filter(it -> !it.getValue().isEmpty())
						.map(this::createProductGroupEntries)
						.collect(toList())
				).ifPresent(product::setEntries);
		return self.save(product);
	}

	@Override
	public Product save(Product product) {
		return productRepository.save(product);
	}

	@Override
	public Category save(Category category) {
		return categoryRepository.save(category);
	}

	@Override
	public void remove(UUID productId) {
		// TODO We need to do some checks first. If we have all fields materialized in Order, it can be a hard delete
		// productRepository.remove(productId);
	}

	private ProductGroupEntry createProductGroupEntries(Map.Entry<ServiceTime, Set<ProductItem>> entry) {
		return new ProductGroupEntry(entry.getKey(), new HashSet<>(entry.getValue()));
	}

	private Map<Category, List<Product>> getProductsByCategory(UUID customerId) {
		val products = listProducts(customerId);
		return products.parallelStream()
				.collect(Collectors.groupingBy(Product::getCategory, TreeMap::new, toList()));
	}

}
