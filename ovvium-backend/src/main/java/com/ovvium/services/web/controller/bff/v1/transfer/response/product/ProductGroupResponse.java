package com.ovvium.services.web.controller.bff.v1.transfer.response.product;

import com.ovvium.services.model.bill.ServiceTime;
import com.ovvium.services.model.product.ProductGroup;
import com.ovvium.services.model.rating.AverageRating;
import com.ovvium.services.web.controller.bff.v1.transfer.response.picture.PictureResponse;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
public final class ProductGroupResponse extends ProductResponse {

	private final Set<DayOfWeek> daysOfWeek;
	private final String startTime;
	private final String endTime;
	private final boolean timeRangeAvailable;

	private final List<Map<String, PictureResponse>> pictures;
	private final Map<ServiceTime, List<ProductResponse>> products;

	public ProductGroupResponse(ProductGroup product, List<Map<String, PictureResponse>> pictureResponses, AverageRating rating, Map<ServiceTime, List<ProductResponse>> products) {
		super(product, rating, "PRODUCT_GROUP");
		this.daysOfWeek = product.getDaysOfWeek();
		this.startTime = product.getStartTime().toString();
		this.endTime = product.getEndTime().toString();
		this.timeRangeAvailable = product.isTimeRangeAvailable(Instant.now());
		this.pictures = pictureResponses;
		this.products = products;
	}

}
