package com.ovvium.services.web.controller.bff.v1.transfer.response.rating;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public final class TotalRatingResponse {

	private final UUID productId;
	private final int rating;
	private final long total;
	private final float percentage;

}
