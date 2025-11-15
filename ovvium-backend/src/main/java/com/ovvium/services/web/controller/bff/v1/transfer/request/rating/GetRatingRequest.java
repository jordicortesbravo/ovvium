package com.ovvium.services.web.controller.bff.v1.transfer.request.rating;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class GetRatingRequest {

	private UUID productId;
	private UUID userId;

}
