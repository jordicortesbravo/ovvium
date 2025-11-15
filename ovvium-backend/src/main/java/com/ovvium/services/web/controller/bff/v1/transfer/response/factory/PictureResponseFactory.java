package com.ovvium.services.web.controller.bff.v1.transfer.response.factory;

import com.ovvium.services.app.config.properties.PictureProperties;
import com.ovvium.services.model.product.Picture;
import com.ovvium.services.web.controller.bff.v1.transfer.response.picture.PictureResponse;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PictureResponseFactory {

	private final PictureProperties pictureProperties;

	public Map<String, PictureResponse> getCropsResponses(Picture picture) {
		val staticsUrl = URI.create(pictureProperties.getStaticsUrl());
		return picture.getCrops().entrySet().stream()
				.collect(Collectors.toMap(
						k -> k.getKey().name().toLowerCase(),
						v -> new PictureResponse(
								UriComponentsBuilder.fromUri(staticsUrl)
										.path(v.getValue().toString())
										.build().toUri()
						)
				));
	}
}
