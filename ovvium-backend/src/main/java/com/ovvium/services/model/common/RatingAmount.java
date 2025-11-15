package com.ovvium.services.model.common;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

import static java.lang.String.format;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = PROTECTED)
public final class RatingAmount {

	public static final int MIN_RATING = 1;
	public static final int MAX_RATING = 5;

	private float amount;

	public RatingAmount(float amount) {
		checkRating(amount);
		this.amount = amount;
	}

	public int asInt() {
		return  Math.round(amount);
	}

	private void checkRating(float rating) {
		if (rating < MIN_RATING || rating > MAX_RATING) {
			throw new IllegalArgumentException(format("Rate must be between %d and %d!", MIN_RATING, MAX_RATING));
		}
	}
}
