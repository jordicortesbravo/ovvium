package com.ovvium.services.web.controller.bff.v1.transfer.request.product;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class CreateProductPictureRequest {

	@NotNull
	private UUID pictureId;

}
