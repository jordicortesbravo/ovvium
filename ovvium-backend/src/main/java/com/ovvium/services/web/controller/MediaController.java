package com.ovvium.services.web.controller;

import com.ovvium.services.model.product.PictureSize;
import com.ovvium.services.service.PictureService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static com.ovvium.services.repository.client.media.FileSystemMediaStore.LOCAL_PATH;
import static org.springframework.http.MediaType.IMAGE_JPEG;


/**
 * Controller to resolve images on local environment.
 */
@Profile("local")
@Controller
@RequiredArgsConstructor
@RequestMapping("/media")
public class MediaController {

	private final PictureService pictureService;

	@GetMapping("/pictures/**/{pictureId}/{size}.jpg")
	public ResponseEntity<byte[]> get(@PathVariable UUID pictureId, @PathVariable String size) throws IOException {
		val picture = pictureService.getOrFail(pictureId);
		val cropUri = picture.getUriOf(PictureSize.valueOf(size.toUpperCase()));
		val file = new File(LOCAL_PATH + cropUri);
		return ResponseEntity.ok().contentType(IMAGE_JPEG)
				.cacheControl(CacheControl.noCache())
				.body(IOUtils.toByteArray(file.toURI()));
	}

}
