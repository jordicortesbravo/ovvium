package com.ovvium.services.web.controller.bff.v1.transfer.response.factory;

import com.ovvium.services.model.customer.Customer;
import com.ovvium.services.web.controller.bff.v1.transfer.response.customer.CustomerResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.picture.PictureResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.URI;

import static com.ovvium.services.model.product.PictureSize.MEDIUM;

@Component
@RequiredArgsConstructor
public class CustomerResponseFactory {

	private final PictureResponseFactory pictureResponseFactory;

	public CustomerResponse create(Customer customer) {
		return new CustomerResponse(customer, getPictureUri(customer));
	}

	private URI getPictureUri(Customer customer) {
		return customer.getPicture().map(pic ->
				pictureResponseFactory.getCropsResponses(pic).get(MEDIUM.name().toLowerCase())
		).map(PictureResponse::getUrl).orElse(null);
	}
}
