package com.ovvium.services.web.controller.bff.v1.transfer.response.common;

import com.ovvium.services.util.common.domain.Page;
import lombok.Data;

import java.util.List;

@Data
public abstract class AbstractPageResponse<T> {

	private final int pageOffset;
	private final int totalPages;
	private final long totalElements;
	private final int numberOfElements;
	private final boolean hasNextPage;
	private final List<T> content;

	public AbstractPageResponse(Page<?> page, List<T> content) {
		this.pageOffset = page.getPageNumber();
		this.totalPages = page.getTotalPages();
		this.totalElements = page.getTotalElements();
		this.numberOfElements = page.getNumberOfElements();
		this.hasNextPage = page.hasNextPage();
		this.content = content;
	}


}

