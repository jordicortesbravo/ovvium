package com.ovvium.services.web.controller.bff.v1.transfer.request.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class GetPageRequest {

	private Integer page;
	private Integer size;

	public Optional<Integer> getPage() {
		return Optional.ofNullable(page);
	}

	public Optional<Integer> getSize() {
		return Optional.ofNullable(size);
	}
}
