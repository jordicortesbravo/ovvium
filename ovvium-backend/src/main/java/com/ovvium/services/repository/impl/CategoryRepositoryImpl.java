package com.ovvium.services.repository.impl;

import com.ovvium.services.model.product.Category;
import com.ovvium.services.model.product.QCategory;
import com.ovvium.services.repository.CategoryRepository;
import com.ovvium.services.util.jpa.core.JpaDefaultRepository;
import lombok.val;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class CategoryRepositoryImpl extends JpaDefaultRepository<Category, UUID> implements CategoryRepository {

    private static final QCategory qCategory = QCategory.category;

    @Override
    public int getLastOrder(UUID customerId) {
        val result = query().where(qCategory.customer.id.eq(customerId))
                .singleResult(qCategory.order.max());
        return result == null ? 0 : result;
    }

    @Override
    public List<Category> listByCustomer(UUID customerId) {
        return list(qCategory.customer.id.eq(customerId));
    }


}
