package com.ovvium.services.repository;

import java.util.Optional;
import java.util.UUID;

import com.ovvium.services.model.product.Product;
import com.ovvium.services.model.rating.Rating;
import com.ovvium.services.model.user.User;
import com.ovvium.services.util.common.domain.Page;
import com.ovvium.services.util.common.domain.PageRequest;
import com.ovvium.services.util.jpa.core.DefaultRepository;

public interface RatingRepository extends DefaultRepository<Rating, UUID> {

	long countOfRating(Product product, Integer rating);

	long countOfProduct(Product product);

	Rating getByProductAndUserOrFail(Product product, User user);

	Integer sumOfProduct(Product product);

	Page<Rating> pageByProduct(PageRequest pageRequest, Product product);

	Optional<Rating> getByProductAndUser(Product product, User user);
}
