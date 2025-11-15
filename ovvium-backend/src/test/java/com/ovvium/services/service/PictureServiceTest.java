package com.ovvium.services.service;

import com.ovvium.services.app.config.properties.PictureProperties;
import com.ovvium.services.model.product.Picture;
import com.ovvium.services.model.product.PictureSize;
import com.ovvium.services.repository.PictureRepository;
import com.ovvium.services.repository.client.media.MediaStore;
import com.ovvium.services.service.impl.PictureServiceImpl;
import com.ovvium.services.util.util.container.Maps;
import com.ovvium.services.web.controller.bff.v1.transfer.request.product.CreatePictureRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static com.ovvium.utils.MockitoUtils.mockRepository;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class PictureServiceTest {

	private PictureService pictureService;

	private MediaStore mediaStore;
	private PictureRepository pictureRepository;
	private PictureProperties pictureProperties;

	@Before
	public void setUp() {
		mediaStore = mock(MediaStore.class);
		pictureProperties = mock(PictureProperties.class);
		pictureRepository = mockRepository(PictureRepository.class);
		pictureService = new PictureServiceImpl(mediaStore, pictureProperties, pictureRepository);
	}

	@Test
	public void given_create_picture_request_with_correct_props_when_create_picture_should_call_media_store_for_each_crop_size() {
		CreatePictureRequest request = new CreatePictureRequest(new byte[0], "filename");

		when(pictureProperties.getBasePath()).thenReturn("/media/pictures");
		when(pictureProperties.getCrop()).thenReturn(new PictureProperties.Crop()
				.setQuality(1)
				.setCropSizes(Maps.mapSS()
						.with("low", "1x1")
						.with("medium", "2x2")
						.with("high", "3x3"))
		);

		Picture picture = pictureService.createPicture(request);

		int numberOfCrops = picture.getCrops().keySet().size();
		verify(mediaStore, times(numberOfCrops)).cropAndUploadPicture(any(), any());
	}

	@Test
	public void given_create_picture_request_with_missing_prop_size_when_create_picture_should_call_media_store_for_each_existent_crop_size() {
		CreatePictureRequest request = new CreatePictureRequest(new byte[0], "filename");

		when(pictureProperties.getBasePath()).thenReturn("/media/pictures");
		when(pictureProperties.getCrop()).thenReturn(new PictureProperties.Crop()
				.setQuality(1)
				.setCropSizes(Maps.mapSS()
						.with("low", "1x1")
						.with("high", "3x3"))
		);

		pictureService.createPicture(request);

		verify(mediaStore, times(2)).cropAndUploadPicture(any(), any());
	}

	@Test
	public void given_create_picture_request_with_correct_props_when_create_picture_should_call_media_store_with_picture_crop_uri() {
		CreatePictureRequest request = new CreatePictureRequest(new byte[0], "filename");

		when(pictureProperties.getBasePath()).thenReturn("/media/pictures");
		when(pictureProperties.getCrop()).thenReturn(new PictureProperties.Crop()
				.setQuality(1)
				.setCropSizes(Maps.mapSS()
						.with("low", "1x1")
						.with("medium", "2x2")
						.with("high", "3x3"))
		);

		Picture picture = pictureService.createPicture(request);

		Map<PictureSize, URI> crops = picture.getCrops();
		int numberOfCrops = crops.keySet().size();
		ArgumentCaptor<URI> captor = ArgumentCaptor.forClass(URI.class);
		verify(mediaStore, times(numberOfCrops)).cropAndUploadPicture(captor.capture(), any());
		List<URI> uris = captor.getAllValues();
		assertThat(uris).containsExactlyInAnyOrder(crops.values().toArray(new URI[0]));
	}
}