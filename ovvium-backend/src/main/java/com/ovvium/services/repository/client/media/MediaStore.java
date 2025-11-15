package com.ovvium.services.repository.client.media;

import com.ovvium.services.util.image.Crop;

import java.net.URI;

public interface MediaStore {

	void cropAndUploadPicture(URI cropUri, Crop.CropConfig cropConfig);

}
