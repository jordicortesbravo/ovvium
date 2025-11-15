package com.ovvium.services.web.controller.bff.v1.transfer.request.rating;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.UUID;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class CreateRatingRequest {

	private UUID productId;
	private UUID userId;
	private int rating;
	private String comment;

}
