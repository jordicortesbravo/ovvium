package com.ovvium.services.repository.client.media;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.ovvium.services.app.config.properties.AwsProperties;
import com.ovvium.services.util.image.Crop.CropConfig;
import com.ovvium.services.util.util.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class AwsMediaStoreTest {

	private AwsMediaStore awsMediaStore;
	private TransferManager transferManager;
	private AwsProperties awsProperties;

	@Before
	public void setUp() throws Exception {
		transferManager = mock(TransferManager.class);
		awsProperties = mock(AwsProperties.class);
		awsMediaStore = new AwsMediaStore(transferManager, awsProperties);
	}

	@Test
	public void given_input_data_when_crop_and_upload_to_aws_then_should_transfer_crop_to_input_stream_correctly() throws IOException {
		URI uri = URI.create("/media/pictures/avatar1.jpg");
		byte[] data = IOUtils.getResourceAsBytes("data/img/avatar1.jpg");
		CropConfig cropConfig = new CropConfig(data, 1, 100, 100);

		awsMediaStore.cropAndUploadPicture(uri, cropConfig);

		ArgumentCaptor<InputStream> captor = ArgumentCaptor.forClass(InputStream.class);
		verify(transferManager, times(1)).upload(any(), any(), captor.capture(), any());
		InputStream is = captor.getValue();
		byte[] bytes = org.apache.commons.io.IOUtils.toByteArray(is);
		assertThat(bytes).isNotEmpty();
	}


	@Test
	public void given_input_data_when_crop_and_upload_to_aws_then_should_set_img_content_type_metadata_on_upload() throws IOException {
		URI uri = URI.create("/media/pictures/avatar1.jpg");
		byte[] data = IOUtils.getResourceAsBytes("data/img/avatar1.jpg");
		CropConfig cropConfig = new CropConfig(data, 1, 100, 100);

		awsMediaStore.cropAndUploadPicture(uri, cropConfig);

		ArgumentCaptor<ObjectMetadata> captor = ArgumentCaptor.forClass(ObjectMetadata.class);
		verify(transferManager, times(1)).upload(any(), any(), any(), captor.capture());
		assertThat(captor.getValue().getContentType()).isEqualTo("image/jpeg");
	}

	@Test
	public void given_uri_when_crop_and_upload_to_aws_then_should_remove_leading_slash_on_key() throws IOException {
		URI uri = URI.create("/media/pictures/avatar1.jpg");
		byte[] data = IOUtils.getResourceAsBytes("data/img/avatar1.jpg");
		CropConfig cropConfig = new CropConfig(data, 1, 100, 100);

		awsMediaStore.cropAndUploadPicture(uri, cropConfig);

		ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
		verify(transferManager, times(1)).upload(any(), captor.capture(), any(), any());
		assertThat(captor.getValue()).isEqualTo("media/pictures/avatar1.jpg");
	}
}