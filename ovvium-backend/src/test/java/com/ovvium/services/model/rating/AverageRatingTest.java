package com.ovvium.services.model.rating;

import com.ovvium.mother.model.RatingMother;
import com.ovvium.services.model.common.RatingAmount;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import static com.ovvium.services.model.common.RatingAmount.MAX_RATING;
import static com.ovvium.services.model.common.RatingAmount.MIN_RATING;

public class AverageRatingTest {

	private static final int TOTAL = 1;

	@Test
	public void givenWrongAverageUnderRange_whenSetAverageRating_mustThrowException() {

		AverageRating averageRating = new AverageRating(RatingMother.rating(MIN_RATING));

		Assertions.assertThatThrownBy(() -> averageRating.setAverage(new RatingAmount(MIN_RATING - 1), TOTAL)) //
				.isInstanceOf(IllegalArgumentException.class) //
				.hasMessage("Rate must be between 1 and 5!");

	}

	@Test
	public void givenWrongAverageUpperRange_wheSetAverageRating_mustThrowException() {

		AverageRating averageRating = new AverageRating(RatingMother.rating(MAX_RATING));

		Assertions.assertThatThrownBy(() -> averageRating.setAverage(new RatingAmount(MAX_RATING + 1), TOTAL)) //
				.isInstanceOf(IllegalArgumentException.class) //
				.hasMessage("Rate must be between 1 and 5!");

	}

}