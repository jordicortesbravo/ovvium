package com.ovvium.services.service.handler;

import com.ovvium.services.model.user.User;
import com.ovvium.services.model.user.event.UserSocialLoggedEvent;
import com.ovvium.services.service.PictureService;
import com.ovvium.services.service.UserService;
import com.ovvium.services.service.event.EventHandler;
import com.ovvium.services.web.controller.bff.v1.transfer.request.product.CreatePictureRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class UpdateUserPictureOnSocialLoginHandler extends EventHandler<UserSocialLoggedEvent> {

	private final UserService userService;
	private final PictureService pictureService;

	@Override
	public void handle(UserSocialLoggedEvent event) {
		User user = userService.getUserOrFail(event.getUserId());
		event.getProfileImage().ifPresent(pictureUri -> {
			val picture = pictureService.createPicture(createPictureRequest(event, pictureUri));
			user.setPicture(picture);
			userService.save(user);
		});
	}

	@SneakyThrows
	private CreatePictureRequest createPictureRequest(UserSocialLoggedEvent event, URI pictureUri) {
		return new CreatePictureRequest(IOUtils.toByteArray(pictureUri),
				FilenameUtils.getName(event.getProfileImage().toString())
		);
	}


}
