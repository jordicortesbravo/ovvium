package com.ovvium.services.web.controller.bff.v1.transfer.response.picture;

import lombok.Getter;

import java.io.Serializable;
import java.net.URI;

@Getter
public final class PictureResponse implements Serializable {

	private final URI url;

	public PictureResponse(URI url) {
		this.url = url;
	}

}
