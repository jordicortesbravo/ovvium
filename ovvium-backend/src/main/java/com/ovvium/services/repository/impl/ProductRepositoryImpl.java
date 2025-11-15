package com.ovvium.services.repository.impl;

import com.ovvium.services.model.product.Product;
import com.ovvium.services.model.product.QProduct;
import com.ovvium.services.repository.ProductRepository;
import com.ovvium.services.util.jpa.core.JpaDefaultRepository;
import lombok.val;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class ProductRepositoryImpl extends JpaDefaultRepository<Product, UUID> implements ProductRepository {

	private static final QProduct qProduct = QProduct.product;

	@Override
	public List<Product> list(UUID customerId) {
		return list(qProduct.customer.id.eq(customerId), qProduct.category.order.asc(), qProduct.order.asc());
	}

	@Override
	public int getLastOrder(UUID customerId, UUID categoryId) {
		val result = query().where(qProduct.customer.id.eq(customerId).and(qProduct.category.id.eq(categoryId)))
				.singleResult(qProduct.order.max());
		return result == null ? 0 : result;
	}
}
