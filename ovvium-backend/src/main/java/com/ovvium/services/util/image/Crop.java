package com.ovvium.services.util.image;

import lombok.Cleanup;
import lombok.Data;
import lombok.SneakyThrows;
import net.coobird.thumbnailator.Thumbnails;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;

public class Crop {

	@Data
	public static class CropConfig {

		private final byte[] source;
		private final int quality;
		private final int width;
		private final int height;

	}

	@SneakyThrows
	public static void create(CropConfig cropConfig, OutputStream outputStream) {
		@Cleanup ByteArrayInputStream inputStream = new ByteArrayInputStream(cropConfig.source);
		Thumbnails.of(inputStream)//
				.size(cropConfig.width, cropConfig.height)//
				.outputQuality(cropConfig.quality)//
				.toOutputStream(outputStream);
	}

}
