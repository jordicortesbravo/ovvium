package com.ovvium.services.model.rating;

import com.ovvium.mother.model.RatingMother;
import com.ovvium.services.model.common.RatingAmount;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import static com.ovvium.services.model.common.RatingAmount.MAX_RATING;
import static com.ovvium.services.model.common.RatingAmount.MIN_RATING;

public class RatingTest {

	@Test
	public void givenWrongRateUnderRange_whenCreateRating_mustThrowException() {

		int rate = MIN_RATING - 1;

		Assertions.assertThatThrownBy(() -> RatingMother.rating(rate)) //
				.isInstanceOf(IllegalArgumentException.class) //
				.hasMessage("Rate must be between 1 and 5!");

	}

	@Test
	public void givenWrongRateUpperRange_whenCreateRating_mustThrowException() {

		int rate = MAX_RATING + 1;

		Assertions.assertThatThrownBy(() -> RatingMother.rating(rate)) //
				.isInstanceOf(IllegalArgumentException.class) //
				.hasMessage("Rate must be between 1 and 5!");

	}

	@Test
	public void givenWrongRateUnderRange_whenChangingRating_mustThrowException() {

		Rating rating = RatingMother.rating(1);

		Assertions.assertThatThrownBy(() -> rating.setRating(new RatingAmount(MIN_RATING - 1))) //
				.isInstanceOf(IllegalArgumentException.class) //
				.hasMessage("Rate must be between 1 and 5!");

	}

	@Test
	public void givenWrongRateUpperRange_whenChangingRating_mustThrowException() {

		Rating rating = RatingMother.rating(1);

		Assertions.assertThatThrownBy(() -> rating.setRating(new RatingAmount(MAX_RATING + 1))) //
				.isInstanceOf(IllegalArgumentException.class) //
				.hasMessage("Rate must be between 1 and 5!");

	}

}