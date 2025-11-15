package com.ovvium.services.web.controller.bff.v1.transfer.response.rating;

import com.ovvium.services.model.rating.Rating;
import com.ovvium.services.util.common.domain.Page;
import com.ovvium.services.web.controller.bff.v1.transfer.response.common.AbstractPageResponse;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public final class RatingsPageResponse extends AbstractPageResponse<RatingResponse> {

	// FIXME Remove this from app and use content instead
	private final List<RatingResponse> ratings;

	public RatingsPageResponse(Page<Rating> page) {
		super(page, page.getContent().stream().map(RatingResponse::new).collect(Collectors.toList()));
		this.ratings = getContent();
	}

}
