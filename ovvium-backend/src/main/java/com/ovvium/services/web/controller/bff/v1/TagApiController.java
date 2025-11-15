package com.ovvium.services.web.controller.bff.v1;

import com.ovvium.services.service.CustomerService;
import com.ovvium.services.web.controller.bff.v1.transfer.response.customer.TagResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.ovvium.services.web.controller.bff.v1.WebConstants.BASE_URI_API_V1;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = BASE_URI_API_V1, produces = APPLICATION_JSON_VALUE)
public class TagApiController {

	private final CustomerService customerService;

	@ResponseStatus(OK)
	@GetMapping("/tags/{tagId}")
	@PreAuthorize("hasRole('USERS')")
	public TagResponse getTag(@PathVariable String tagId) {
		return customerService.getTag(tagId);
	}
}
