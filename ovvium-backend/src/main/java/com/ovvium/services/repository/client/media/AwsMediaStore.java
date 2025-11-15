package com.ovvium.services.repository.client.media;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.ovvium.services.app.config.properties.AwsProperties;
import com.ovvium.services.util.image.Crop;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;

@Component
@Profile("!local")
@RequiredArgsConstructor
public class AwsMediaStore implements MediaStore {

	private final TransferManager transferManager;
	private final AwsProperties awsProperties;

	@Override
	@SneakyThrows
	public void cropAndUploadPicture(URI cropUri, Crop.CropConfig cropConfig) {
		byte[] cropBytes = createCrop(cropConfig);
		@Cleanup val is = new ByteArrayInputStream(cropBytes);
		transferManager.upload(awsProperties.getS3Bucket(), toKey(cropUri), is, getImageMetadata(cropBytes.length));
	}

	@SneakyThrows
	private byte[] createCrop(Crop.CropConfig cropConfig) {
		@Cleanup val bos = new ByteArrayOutputStream();
		Crop.create(cropConfig, bos);
		return bos.toByteArray();
	}

	private String toKey(URI cropUri) {
		return StringUtils.removeStart(cropUri.toString(), "/");
	}

	// We need to set ObjectMetadata content-type for pictures or the browser
	// will download the files instead of showing them as images
	private ObjectMetadata getImageMetadata(int contentLength) {
		val meta = new ObjectMetadata();
		meta.setContentType("image/jpeg");
		meta.setContentLength(contentLength);
		return meta;
	}

}
