package com.ovvium.services.service;

import com.ovvium.services.model.rating.AverageRating;
import com.ovvium.services.model.rating.Rating;
import com.ovvium.services.util.ovvium.cache.CollectionWrapper;
import com.ovvium.services.web.controller.bff.v1.transfer.request.rating.CreateRatingRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.rating.GetRatingRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.rating.GetRatingsPageRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.rating.UpdateRatingRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.response.rating.RatingResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.rating.RatingsPageResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.rating.TotalRatingResponse;

import java.util.Optional;
import java.util.UUID;

public interface RatingService {

	Rating create(CreateRatingRequest createRatingRequest);

	RatingResponse getRatingByProductAndUser(GetRatingRequest getRatingRequest);

	Rating getRating(UUID ratingId);

	AverageRating getAverageRatingOrFail(UUID averageId);

	Optional<AverageRating> getAverageRating(UUID productId);

	CollectionWrapper<TotalRatingResponse> getTotalRatings(UUID productId);

	void updateRating(UpdateRatingRequest ratingId);

	RatingsPageResponse pageRatings(GetRatingsPageRequest request);

	Rating save(Rating rating);

	AverageRating save(AverageRating rating);
}
