package com.ovvium.services.repository.client.media;

import com.ovvium.services.util.image.Crop;
import com.ovvium.services.util.util.basic.Path;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.net.URI;

@Slf4j
@Component
@Profile("local")
@RequiredArgsConstructor
public class FileSystemMediaStore implements MediaStore {

	public final static String LOCAL_PATH = "/tmp/ovvium/";

	@Override
	@SneakyThrows
	public void cropAndUploadPicture(URI cropUri, Crop.CropConfig cropConfig) {
		val file = Path.of(LOCAL_PATH).add(cropUri.toString()).toFile();
		file.getParentFile().mkdirs();
		@Cleanup val fos = new FileOutputStream(file);
		Crop.create(cropConfig, fos);
	}

}
