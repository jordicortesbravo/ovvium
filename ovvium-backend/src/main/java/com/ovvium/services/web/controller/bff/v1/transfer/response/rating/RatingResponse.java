package com.ovvium.services.web.controller.bff.v1.transfer.response.rating;

import com.ovvium.services.model.rating.Rating;
import com.ovvium.services.web.controller.bff.v1.transfer.response.common.ResourceIdResponse;
import lombok.Getter;

@Getter
public final class RatingResponse extends ResourceIdResponse {

	private final int rating;
	private final String userName;
	private final String comment;
	private final String updated;

	public RatingResponse(Rating rating) {
		super(rating);
		this.rating = rating.getRating().asInt();
		this.userName = rating.getUserName();
		this.comment = rating.getComment().orElse(null);
		this.updated = rating.getUpdated().toString(); // TODO well formatted
	}
}
