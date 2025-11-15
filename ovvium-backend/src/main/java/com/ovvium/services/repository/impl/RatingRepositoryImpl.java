package com.ovvium.services.repository.impl;

import com.ovvium.services.model.product.Product;
import com.ovvium.services.model.rating.QRating;
import com.ovvium.services.model.rating.Rating;
import com.ovvium.services.model.user.User;
import com.ovvium.services.repository.RatingRepository;
import com.ovvium.services.util.common.domain.Page;
import com.ovvium.services.util.common.domain.PageRequest;
import com.ovvium.services.util.jpa.core.JpaDefaultRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class RatingRepositoryImpl extends JpaDefaultRepository<Rating, UUID> implements RatingRepository {

	private static final QRating qRating = QRating.rating1;

	@Override
	public long countOfRating(Product product, Integer rating) {
		return count(qRating.productId.eq(product.getId()).and(qRating.rating.amount.eq((float) rating)));
	}

	@Override
	public long countOfProduct(Product product) {
		return count(qRating.productId.eq(product.getId()));
	}

	@Override
	public Page<Rating> pageByProduct(PageRequest pageRequest, Product product) {
		return super.page(qRating.productId.eq(product.getId()), pageRequest);
	}

	@Override
	public Optional<Rating> getByProductAndUser(Product product, User user) {
		return get(qRating.productId.eq(product.getId()).and(qRating.userId.eq(user.getId())));
	}

	@Override
	public Rating getByProductAndUserOrFail(Product product, User user) {
		return getOrFail(qRating.productId.eq(product.getId()).and(qRating.userId.eq(user.getId())));
	}

	@Override
	public Integer sumOfProduct(Product product) {
		return query(qRating.productId.eq(product.getId())) //
				.singleResult(qRating.rating.amount.sum()).intValue();
	}

}
