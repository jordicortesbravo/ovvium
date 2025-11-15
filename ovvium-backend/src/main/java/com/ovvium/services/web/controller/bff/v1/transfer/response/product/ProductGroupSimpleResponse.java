package com.ovvium.services.web.controller.bff.v1.transfer.response.product;

import com.ovvium.services.model.product.ProductGroup;
import com.ovvium.services.model.rating.AverageRating;
import com.ovvium.services.web.controller.bff.v1.transfer.response.picture.PictureResponse;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.Instant;
import java.util.Map;
import java.util.Set;

@Getter
public final class ProductGroupSimpleResponse extends ProductResponse {

	private final Set<DayOfWeek> daysOfWeek;
	private final String startTime;
	private final String endTime;
	private final boolean timeRangeAvailable;
	private final Map<String, PictureResponse> coverPicture;

	public ProductGroupSimpleResponse(ProductGroup product, Map<String, PictureResponse> pictureResponse, AverageRating rating) {
		super(product, rating, "PRODUCT_GROUP");
		this.daysOfWeek = product.getDaysOfWeek();
		this.startTime = product.getStartTime().toString();
		this.endTime = product.getEndTime().toString();
		this.coverPicture = pictureResponse;
		this.timeRangeAvailable = product.isTimeRangeAvailable(Instant.now());
	}

}
