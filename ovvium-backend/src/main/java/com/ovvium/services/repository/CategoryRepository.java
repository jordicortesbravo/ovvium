package com.ovvium.services.repository;

import com.ovvium.services.model.product.Category;
import com.ovvium.services.util.jpa.core.DefaultRepository;

import java.util.List;
import java.util.UUID;

public interface CategoryRepository extends DefaultRepository<Category, UUID> {

    int getLastOrder(UUID customerId);

    List<Category> listByCustomer(UUID customerId);

}
