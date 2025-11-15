package com.ovvium.services.model.rating;

import com.ovvium.services.model.common.RatingAmount;
import com.ovvium.services.util.ovvium.domain.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.Entity;

import static com.ovvium.services.util.ovvium.base.Preconditions.checkNotNull;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Accessors(chain = true)
@NoArgsConstructor(access = PROTECTED)
public class AverageRating extends BaseEntity {

	private RatingAmount average;

	private long totalRatings;

	public AverageRating(Rating firstRating) {
		super(checkNotNull(firstRating, "Rating can't be null").getProductId());
		setAverage(firstRating.getRating(), 1);
	}

	public AverageRating setAverage(RatingAmount average, long total) {
		this.average = checkNotNull(average, "Rating amount can't be null");
		this.totalRatings = checkTotal(total);
		return this;
	}

	public int getAverageAsInt() {
		return average.asInt();
	}

	private long checkTotal(long total) {
		if (total < 0) {
			throw new IllegalArgumentException("Total must be greater or equal to 0");
		}
		return total;
	}

}
