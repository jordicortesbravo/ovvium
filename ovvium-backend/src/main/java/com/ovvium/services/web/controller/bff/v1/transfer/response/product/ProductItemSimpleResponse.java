package com.ovvium.services.web.controller.bff.v1.transfer.response.product;

import com.ovvium.services.model.product.Product;
import com.ovvium.services.model.rating.AverageRating;
import com.ovvium.services.web.controller.bff.v1.transfer.response.picture.PictureResponse;
import lombok.Getter;

import java.util.Map;

@Getter
public final class ProductItemSimpleResponse extends ProductResponse {

	private final Map<String, PictureResponse> coverPicture;

	public ProductItemSimpleResponse(Product product, Map<String, PictureResponse> pictureResponse, AverageRating rating) {
		super(product, rating, "PRODUCT_ITEM");
		this.coverPicture = pictureResponse;
	}

}
