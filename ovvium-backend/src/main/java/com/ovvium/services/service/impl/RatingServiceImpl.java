package com.ovvium.services.service.impl;

import com.ovvium.services.model.common.RatingAmount;
import com.ovvium.services.model.exception.OvviumDomainException;
import com.ovvium.services.model.product.Product;
import com.ovvium.services.model.rating.AverageRating;
import com.ovvium.services.model.rating.Rating;
import com.ovvium.services.model.user.User;
import com.ovvium.services.repository.AverageRatingRepository;
import com.ovvium.services.repository.RatingRepository;
import com.ovvium.services.service.AccountService;
import com.ovvium.services.service.ProductService;
import com.ovvium.services.service.RatingService;
import com.ovvium.services.util.common.domain.PageRequest;
import com.ovvium.services.util.common.domain.Pageable;
import com.ovvium.services.util.ovvium.cache.CollectionWrapper;
import com.ovvium.services.web.controller.bff.v1.transfer.request.rating.CreateRatingRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.rating.GetRatingRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.rating.GetRatingsPageRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.rating.UpdateRatingRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.response.rating.RatingResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.rating.RatingsPageResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.rating.TotalRatingResponse;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.ovvium.services.app.constant.Caches.*;
import static com.ovvium.services.model.common.RatingAmount.MAX_RATING;
import static com.ovvium.services.model.common.RatingAmount.MIN_RATING;
import static com.ovvium.services.model.exception.ErrorCode.RATING_ALREADY_EXISTS;
import static com.ovvium.services.util.common.domain.SimplePage.FIRST_PAGE;
import static com.ovvium.services.util.common.domain.Sort.desc;
import static com.ovvium.services.util.ovvium.domain.entity.TimestampedEntity.UPDATED;
import static java.util.Collections.emptySet;

@Service
@Transactional
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

	private final ProductService productService;
	private final AccountService accountService;
	private final RatingRepository ratingRepository;
	private final AverageRatingRepository averageRatingRepository;

	@Autowired
	private RatingService self;

	@Override
	public Rating create(CreateRatingRequest createRatingRequest) {
		Product product = productService.getProduct(createRatingRequest.getProductId());
		User user = accountService.getUser(createRatingRequest.getUserId());
		if (ratingRepository.getByProductAndUser(product, user).isPresent()) {
			throw new OvviumDomainException(RATING_ALREADY_EXISTS);
		}
		val amount = new RatingAmount(createRatingRequest.getRating());
		Rating newRating = new Rating(product, user, amount) //
				.setComment(createRatingRequest.getComment());
		// Que siempre exista al menos una media creada por producto
		if (getAverageRating(product.getId()).isEmpty()) {
			self.save(new AverageRating(newRating));
		}
		return self.save(newRating);
	}

	@Override
	@Cacheable(value = RATINGS_BY_PRODUCT_AND_USER, key = "#request.productId + '_' + #request.userId")
	public RatingResponse getRatingByProductAndUser(GetRatingRequest request) {
		Product product = productService.getProduct(request.getProductId());
		User user = accountService.getUser(request.getUserId());
		return new RatingResponse(ratingRepository.getByProductAndUserOrFail(product, user));
	}

	@Override
	public Rating getRating(UUID ratingId) {
		return ratingRepository.getOrFail(ratingId);
	}

	@Override
	public AverageRating getAverageRatingOrFail(UUID productId) {
		return averageRatingRepository.getOrFail(productId);
	}

	@Override
	public Optional<AverageRating> getAverageRating(UUID productId) {
		return averageRatingRepository.get(productId);
	}

	@Override
	@Cacheable(value = TOTAL_RATINGS)
	public CollectionWrapper<TotalRatingResponse> getTotalRatings(UUID productId) {
		val product = productService.getProduct(productId);
		val totalRatingResponses = IntStream.rangeClosed(MIN_RATING, MAX_RATING)//
				.boxed()//
				.map(i -> createTotalRating(product, i))//
				.collect(Collectors.toList());
		return CollectionWrapper.of(productId, totalRatingResponses);
	}

	@Override
	public void updateRating(UpdateRatingRequest request) {
		Rating rating = ratingRepository.getOrFail(request.getRatingId());
		request.getRating() //
				.map(RatingAmount::new)//
				.map(rating::setRating) //
				.orElseThrow(() -> new IllegalArgumentException("New rate is necessary to update rating."));
		request.getComment().ifPresent(rating::setComment);
		self.save(rating);
	}

	@Override
	public RatingsPageResponse pageRatings(GetRatingsPageRequest request) {
		// FIXME This is wrong, should be 1 instead. Check InvoiceService. App request should be fixed too
		Integer page = request.getPage().orElse(FIRST_PAGE);
		Integer size = request.getSize().orElse(20);
		val pageRequest = PageRequest.of( //
				emptySet(), //
				new Pageable(page, size, desc(UPDATED)) //
		);
		val ratingsPage = ratingRepository.pageByProduct( //
				pageRequest, //
				productService.getProduct(request.getProductId()) //
		);
		return new RatingsPageResponse(ratingsPage);
	}

	@Override
	@Caching(evict = {
			@CacheEvict(value = TOTAL_RATINGS, key = "#rating.productId"),
			@CacheEvict(value = RATINGS_BY_PRODUCT_AND_USER, key = "#rating.productId + '_' + #rating.userId")
	})
	public Rating save(Rating rating) {
		Rating newRating = ratingRepository.save(rating);
		updateAverage(rating);
		return newRating;
	}

	@Override
	// FIXME Buscar una mejor forma de hacer esto! Hay acoplamiento con ProductResponse
	@Caching(evict = {
			@CacheEvict(value = PRODUCTS, key = "#rating.id"),
			@CacheEvict(value = PRODUCTS_BY_CUSTOMER, allEntries = true)
	})
	public AverageRating save(AverageRating rating) {
		return averageRatingRepository.save(rating);
	}

	private AverageRating updateAverage(Rating rating) {
		AverageRating averageRating = getAverageRatingOrFail(rating.getProductId());
		Product product = productService.getProduct(rating.getProductId());
		Integer sumOfProduct = ratingRepository.sumOfProduct(product);
		long countOfProduct = ratingRepository.countOfProduct(product);
		val result = new RatingAmount((float) sumOfProduct / countOfProduct);
		averageRating.setAverage(result, countOfProduct);
		return self.save(averageRating);
	}

	private TotalRatingResponse createTotalRating(Product product, Integer rating) {
		long countOfRating = ratingRepository.countOfRating(product, rating);
		long countOfProduct = ratingRepository.countOfProduct(product);
		float percentage = (countOfRating * 100.0f) / countOfProduct;
		return new TotalRatingResponse(product.getId(), rating, countOfRating, percentage);
	}
}
