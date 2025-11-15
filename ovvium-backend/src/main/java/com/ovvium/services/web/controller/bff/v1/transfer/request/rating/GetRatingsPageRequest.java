package com.ovvium.services.web.controller.bff.v1.transfer.request.rating;

import com.ovvium.services.web.controller.bff.v1.transfer.request.common.GetPageRequest;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.UUID;

@Data
@Accessors(chain = true)
public class GetRatingsPageRequest extends GetPageRequest {

	private UUID productId;

	public GetRatingsPageRequest(Integer page, Integer size, UUID productId) {
		super(page, size);
		this.productId = productId;
	}
}
