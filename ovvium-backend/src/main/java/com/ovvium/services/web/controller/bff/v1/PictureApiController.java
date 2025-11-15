package com.ovvium.services.web.controller.bff.v1;

import com.ovvium.services.service.PictureService;
import com.ovvium.services.util.ovvium.spring.ControllerUtils;
import com.ovvium.services.web.controller.bff.v1.transfer.request.product.CreatePictureRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.response.common.ResourceIdResponse;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.ovvium.services.web.controller.bff.v1.WebConstants.BASE_URI_API_V1;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = BASE_URI_API_V1, produces = APPLICATION_JSON_VALUE)
public class PictureApiController {

	private final PictureService pictureService;

	@PostMapping("/pictures")
	@ResponseStatus(CREATED)
	public ResourceIdResponse uploadPicture(MultipartFile picture) throws IOException {
		ControllerUtils.checkPictureMultipart(picture);
		val request = new CreatePictureRequest(
				picture.getBytes(),
				picture.getName()
		);
		return new ResourceIdResponse(pictureService.createPicture(request));
	}

}
