package com.ovvium.services.service;

import com.ovvium.mother.model.ProductMother;
import com.ovvium.mother.model.RatingMother;
import com.ovvium.mother.model.UserMother;
import com.ovvium.services.model.exception.OvviumDomainException;
import com.ovvium.services.model.product.Product;
import com.ovvium.services.model.user.User;
import com.ovvium.services.repository.AverageRatingRepository;
import com.ovvium.services.repository.RatingRepository;
import com.ovvium.services.service.impl.RatingServiceImpl;
import com.ovvium.services.util.util.reflection.ReflectionUtils;
import com.ovvium.services.web.controller.bff.v1.transfer.request.rating.CreateRatingRequest;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static com.ovvium.mother.model.ProductMother.PATATAS_BRAVAS_ID;
import static com.ovvium.mother.model.UserMother.USER_JORDI_ID;
import static com.ovvium.services.model.common.RatingAmount.MAX_RATING;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RatingServiceTest {

	// SUT
	private RatingService ratingService;

	private ProductService productService;
	private AccountService accountService;
	private RatingRepository ratingRepository;
	private AverageRatingRepository averageRatingRepository;

	@Before
	public void setUp() throws Exception {
		productService = mock(ProductService.class);
		accountService = mock(AccountService.class);
		ratingRepository = mock(RatingRepository.class);
		averageRatingRepository = mock(AverageRatingRepository.class);

		ratingService = new RatingServiceImpl(productService, accountService, ratingRepository,
				averageRatingRepository);
		ReflectionUtils.set(ratingService, "self", ratingService);
	}

	@Test
	public void givenExistingRating_whenCreateRating_mustThrowException() {

		CreateRatingRequest createRatingRequest = new CreateRatingRequest() //
				.setProductId(PATATAS_BRAVAS_ID) //
				.setUserId(USER_JORDI_ID) //
				.setRating(MAX_RATING);

		Product product = ProductMother.getPatatasBravasProduct();
		User user = UserMother.getUserJordi();
		when(productService.getProduct(PATATAS_BRAVAS_ID)).thenReturn(product);
		when(accountService.getUser(USER_JORDI_ID)).thenReturn(user);
		when(ratingRepository.getByProductAndUser(product, user)).thenReturn(Optional.of(RatingMother.rating(3)));

		assertThatThrownBy(() -> ratingService.create(createRatingRequest)) //
				.isInstanceOf(OvviumDomainException.class) //
				.hasMessageContaining("Rating already exists for this product and user.");
	}
}