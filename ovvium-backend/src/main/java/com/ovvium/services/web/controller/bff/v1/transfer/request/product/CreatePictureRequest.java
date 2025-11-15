package com.ovvium.services.web.controller.bff.v1.transfer.request.product;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@RequiredArgsConstructor
public class CreatePictureRequest {

	private final byte[] data;
	private final String filename;

}
