package com.ovvium.services.service;

import com.ovvium.services.model.product.Picture;
import com.ovvium.services.web.controller.bff.v1.transfer.request.product.CreatePictureRequest;

import java.util.UUID;

public interface PictureService {

	Picture createPicture(CreatePictureRequest request);

	Picture getOrFail(UUID pictureId);
}
