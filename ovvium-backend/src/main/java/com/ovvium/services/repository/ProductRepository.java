package com.ovvium.services.repository;

import java.util.List;
import java.util.UUID;

import com.ovvium.services.model.product.Product;
import com.ovvium.services.util.jpa.core.DefaultRepository;


public interface ProductRepository extends DefaultRepository<Product, UUID> {

	List<Product> list(UUID customerId);

	int getLastOrder(UUID customerId, UUID categoryId);

}
