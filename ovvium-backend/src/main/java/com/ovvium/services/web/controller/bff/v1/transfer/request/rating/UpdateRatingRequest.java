package com.ovvium.services.web.controller.bff.v1.transfer.request.rating;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Optional;
import java.util.UUID;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class UpdateRatingRequest {

	private UUID ratingId;
	private Integer rating;
	private String comment;

	public Optional<Integer> getRating() {
		return Optional.ofNullable(rating);
	}

	public Optional<String> getComment() {
		return Optional.ofNullable(comment);
	}
}
