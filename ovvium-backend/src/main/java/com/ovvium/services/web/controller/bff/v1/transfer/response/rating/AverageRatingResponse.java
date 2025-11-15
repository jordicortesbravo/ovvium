package com.ovvium.services.web.controller.bff.v1.transfer.response.rating;

import com.ovvium.services.model.rating.AverageRating;
import com.ovvium.services.web.controller.bff.v1.transfer.response.common.ResourceIdResponse;
import lombok.Getter;

import java.util.UUID;

@Getter
public final class AverageRatingResponse extends ResourceIdResponse {

	private final float average;
	private final UUID productId;

	public AverageRatingResponse(AverageRating averageRating) {
		super(averageRating);
		this.average = averageRating.getAverage().getAmount();
		this.productId = averageRating.getId();
	}

}
