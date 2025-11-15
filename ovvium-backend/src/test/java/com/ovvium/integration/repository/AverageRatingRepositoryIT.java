package com.ovvium.integration.repository;

import com.ovvium.integration.DbDataConstants;
import com.ovvium.services.model.common.RatingAmount;
import com.ovvium.services.model.rating.AverageRating;
import com.ovvium.services.model.rating.Rating;
import com.ovvium.services.repository.AverageRatingRepository;
import com.ovvium.services.repository.ProductRepository;
import com.ovvium.services.repository.UserRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class AverageRatingRepositoryIT extends AbstractRepositoryIT {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ProductRepository productRepository;

	// SUT
	@Autowired
	private AverageRatingRepository repository;

	@Test
	public void given_saved_averagerating_when_getOrFail_then_check_averagerating_is_saved() {
		Rating rating = createRating();

		AverageRating averageRating = new AverageRating(rating);
		repository.save(averageRating);

		AverageRating savedRating = repository.getOrFail(averageRating.getId());

		assertThat(savedRating.getAverage()).isEqualTo(rating.getRating());
	}

	@Test
	public void given_saved_averagerating_when_list_then_check_averagerating_is_saved() {
		final Rating rating = createRating();
		final int oldSize = repository.list().size();

		AverageRating entity = new AverageRating(rating);
		repository.save(entity);

		List<AverageRating> averageRatings = repository.list();

		assertThat(averageRatings).hasSize(oldSize + 1);
		assertThat(averageRatings).contains(entity);
	}

	private Rating createRating() {
		return new Rating(productRepository.getOrFail(DbDataConstants.PRODUCT_1_ID),
				userRepository.getOrFail(DbDataConstants.USER_1_ID),
				new RatingAmount(3));
	}

}
