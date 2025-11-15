package com.ovvium.services.service;

import com.ovvium.services.model.product.*;
import com.ovvium.services.transfer.command.category.CreateCategoryCommand;
import com.ovvium.services.transfer.command.category.UpdateCategoryCommand;
import com.ovvium.services.transfer.command.product.*;

import java.util.List;
import java.util.UUID;

public interface ProductService {

	Category createCategory(CreateCategoryCommand command);

	Category getCategory(UUID categoryId);

	Category updateCategory(UpdateCategoryCommand command);

	List<Category> listCategories(UUID customerId);

	Product getProduct(UUID productId);

	List<Product> listProducts(UUID customerId);

	ProductItem create(CreateProductItemCommand command);

	Product update(UpdateProductCommand command);

	Product updateItem(UpdateProductItemCommand command);

	Product updateGroup(UpdateProductGroupCommand command);

	ProductGroup createGroup(CreateProductGroupCommand command);

	Product save(Product product);

	Category save(Category category);

	void remove(UUID productId);

	Product addPictureToProduct(Product product, Picture picture);

}
