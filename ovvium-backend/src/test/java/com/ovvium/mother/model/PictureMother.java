package com.ovvium.mother.model;

import com.ovvium.services.model.product.Picture;
import com.ovvium.services.util.util.reflection.ReflectionUtils;
import lombok.experimental.UtilityClass;

import java.net.URI;
import java.util.UUID;


@UtilityClass
public class PictureMother {

	public static final UUID COVER_PICTURE_ID = UUID.fromString("2ab74b10-1b06-437a-b4b3-f8a5fc4679d4");
	public static final UUID USER_PICTURE_ID = UUID.fromString("c59d9777-78cc-438a-aa77-4dbcb281bc9d");

	public static Picture getCoverPicture() {
		Picture picture = new Picture("/basepath", "cover.jpg");
		ReflectionUtils.set(picture, "id", COVER_PICTURE_ID);
		ReflectionUtils.set(picture, "baseUri", URI.create("/basepath/20200101/" + COVER_PICTURE_ID));
		return picture;
	}

	public static Picture getUserPicture() {
		Picture picture = new Picture("/basepath", "user.jpg");
		ReflectionUtils.set(picture, "id", USER_PICTURE_ID);
		ReflectionUtils.set(picture, "baseUri", URI.create("/basepath/20200101/" + USER_PICTURE_ID));
		return picture;
	}

	public static Picture anyPicture() {
		return new Picture("/basepath", "cover.jpg");
	}

}
