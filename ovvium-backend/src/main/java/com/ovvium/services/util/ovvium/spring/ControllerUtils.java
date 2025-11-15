package com.ovvium.services.util.ovvium.spring;

import lombok.experimental.UtilityClass;
import org.springframework.web.multipart.MultipartFile;

import static com.ovvium.services.util.ovvium.base.Preconditions.check;
import static com.ovvium.services.util.ovvium.base.Preconditions.checkNotNull;

@UtilityClass
public class ControllerUtils {

	public void checkPictureMultipart(MultipartFile picture) {
		checkNotNull(picture, "Picture cannot be null");
		check(picture.getContentType().toLowerCase().startsWith("image/"), "File uploaded is not image format.");
	}

}
