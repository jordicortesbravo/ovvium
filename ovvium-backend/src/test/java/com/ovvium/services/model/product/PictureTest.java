package com.ovvium.services.model.product;

import org.junit.Test;

import java.net.URI;
import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PictureTest {

	@Test
	public void given_wrong_base_url_when_create_picture_should_check_base_url_is_uri() {
		final String basePath = ":wrongp@th_";

		assertThatThrownBy(() ->
				new Picture(basePath, "Filename.jpg")
		).isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("Could not create URI object");
	}

	@Test
	public void given_correct_base_url_when_get_picture_crops_should_return_correct_uris() {
		final String basePath = "/media/pictures";

		Picture picture = new Picture(basePath, "Filename.jpg");

		Map<PictureSize, URI> crops = picture.getCrops();
		String localDate = LocalDate.now().toString().replace("-","");
		String baseUri = "/media/pictures/" + localDate + "/" + picture.getId();
		assertThat(crops.keySet()).contains(PictureSize.values());
		assertThat(crops.get(PictureSize.LOW)).isEqualTo(URI.create(baseUri + "/low.jpg"));
		assertThat(crops.get(PictureSize.MEDIUM)).isEqualTo(URI.create(baseUri + "/medium.jpg"));
		assertThat(crops.get(PictureSize.HIGH)).isEqualTo(URI.create(baseUri + "/high.jpg"));
	}
}