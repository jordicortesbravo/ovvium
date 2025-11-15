package com.ovvium.mother.model;

import com.ovvium.services.model.common.RatingAmount;
import com.ovvium.services.model.rating.Rating;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RatingMother {

	public static Rating rating(int rating) {
		return new Rating(ProductMother.getPatatasBravasProduct(), UserMother.getUserJorge(), new RatingAmount(rating));
	}

}
