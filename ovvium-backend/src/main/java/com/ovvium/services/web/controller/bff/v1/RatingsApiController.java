package com.ovvium.services.web.controller.bff.v1;

import com.ovvium.services.model.rating.AverageRating;
import com.ovvium.services.model.rating.Rating;
import com.ovvium.services.service.RatingService;
import com.ovvium.services.web.controller.bff.v1.transfer.request.rating.CreateRatingRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.rating.GetRatingRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.rating.GetRatingsPageRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.rating.UpdateRatingRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.response.common.ResourceIdResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.rating.AverageRatingResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.rating.RatingResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.rating.RatingsPageResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.rating.TotalRatingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.ovvium.services.web.controller.bff.v1.WebConstants.BASE_URI_API_V1;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = BASE_URI_API_V1, produces = APPLICATION_JSON_VALUE)
public class RatingsApiController {

	private final RatingService ratingService;

	@ResponseStatus(CREATED)
	@PostMapping("/ratings")
	@PreAuthorize("hasRole('USERS') and @auth.isSameUser(#request.userId)")
	public ResourceIdResponse createRating(@RequestBody CreateRatingRequest request) {
		Rating rating = ratingService.create(request);
		return new ResourceIdResponse(rating);
	}

	@ResponseStatus(OK)
	@GetMapping("/ratings")
	public RatingResponse getRatingByProductAndUser(@RequestParam UUID productId, @RequestParam UUID userId) {
		return ratingService.getRatingByProductAndUser(new GetRatingRequest(productId, userId));
	}

	@ResponseStatus(OK)
	@GetMapping("/ratings/{ratingId}")
	public RatingResponse getRating(@PathVariable UUID ratingId) {
		return new RatingResponse(ratingService.getRating(ratingId));
	}

	@ResponseStatus(OK)
	@GetMapping("/ratings/page")
	public RatingsPageResponse pageRatings(@RequestParam UUID productId,
										   @RequestParam(required = false) Integer page,
										   @RequestParam(required = false) Integer size) {
		return ratingService.pageRatings(new GetRatingsPageRequest(page, size, productId));
	}

	@ResponseStatus(OK)
	@PatchMapping("/ratings/{ratingId}")
	@PreAuthorize("hasRole('USERS') and @auth.isLoggedUserRating(#ratingId)")
	public void updateRating(@PathVariable UUID ratingId, @RequestBody UpdateRatingRequest request) {
		request.setRatingId(ratingId);
		ratingService.updateRating(request);
	}

	@ResponseStatus(OK)
	@GetMapping("/ratings/totals")
	public List<TotalRatingResponse> getTotalRatings(@RequestParam UUID productId) {
		return ratingService.getTotalRatings(productId).toList();
	}

	@ResponseStatus(OK)
	@GetMapping("/ratings/averages/{productId}")
	public AverageRatingResponse getAverage(@PathVariable UUID productId) {
		AverageRating averageRating = ratingService.getAverageRatingOrFail(productId);
		return new AverageRatingResponse(averageRating);
	}

}
