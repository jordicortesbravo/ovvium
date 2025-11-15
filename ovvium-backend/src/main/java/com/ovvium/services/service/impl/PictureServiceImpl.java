package com.ovvium.services.service.impl;

import com.ovvium.services.app.config.properties.PictureProperties;
import com.ovvium.services.model.product.Picture;
import com.ovvium.services.repository.PictureRepository;
import com.ovvium.services.repository.client.media.MediaStore;
import com.ovvium.services.service.PictureService;
import com.ovvium.services.util.image.Crop;
import com.ovvium.services.web.controller.bff.v1.transfer.request.product.CreatePictureRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static java.lang.Integer.parseInt;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PictureServiceImpl implements PictureService {

	private final MediaStore mediaStore;
	private final PictureProperties pictureProperties;
	private final PictureRepository pictureRepository;

	@Override
	@SneakyThrows
	public Picture createPicture(CreatePictureRequest request) {
		val picture = new Picture(
				pictureProperties.getBasePath(),
				request.getFilename()
		);
		createCropsAndUpload(picture, request);
		return pictureRepository.save(picture);
	}

	@Override
	public Picture getOrFail(UUID pictureId) {
		return pictureRepository.getOrFail(pictureId);
	}

	@SneakyThrows
	private void createCropsAndUpload(Picture picture, CreatePictureRequest request) {
		val cropProps = pictureProperties.getCrop();
		val quality = cropProps.getQuality();
		val cropSizes = cropProps.getCropSizes();
		picture.getCrops().forEach((size, cropUri) -> {
			val cropSize = cropSizes.get(size.name().toLowerCase());
			if (cropSize != null) {
				val sizeValue = cropSize.split("x");
				val cropConfig = new Crop.CropConfig(request.getData(), quality, parseInt(sizeValue[0]), parseInt(sizeValue[1]));
				mediaStore.cropAndUploadPicture(cropUri, cropConfig);
			} else {
				log.error("Crop size not found on property map: {}", size);
			}
		});
	}


}
