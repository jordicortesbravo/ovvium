package com.ovvium.services.web.controller.bff.v1.transfer.response.product;

import com.ovvium.services.model.product.Product;
import com.ovvium.services.model.rating.AverageRating;
import com.ovvium.services.web.controller.bff.v1.transfer.response.picture.PictureResponse;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public final class ProductItemResponse extends ProductResponse {

	private final List<Map<String, PictureResponse>> pictures;

	public ProductItemResponse(Product product, List<Map<String, PictureResponse>> pictureResponses, AverageRating rating) {
		super(product, rating, "PRODUCT_ITEM");
		this.pictures = pictureResponses;
	}

}
