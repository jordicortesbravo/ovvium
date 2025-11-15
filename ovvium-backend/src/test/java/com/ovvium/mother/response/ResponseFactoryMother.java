package com.ovvium.mother.response;

import com.ovvium.services.app.config.properties.PictureProperties;
import com.ovvium.services.repository.AverageRatingRepository;
import com.ovvium.services.service.CustomerService;
import com.ovvium.services.web.controller.bff.v1.transfer.response.factory.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ResponseFactoryMother {

	public static PictureResponseFactory aPictureResponseFactory() {
		final PictureProperties props = mock(PictureProperties.class);
		when(props.getStaticsUrl()).thenReturn("https://statics.ovvium.test/media/pictures");
		return new PictureResponseFactory(props);
	}

	public static UserResponseFactory anUserResponseFactory() {
		return new UserResponseFactory(aPictureResponseFactory());
	}

	public static CustomerResponseFactory aCustomerResponseFactory() {
		return new CustomerResponseFactory(aPictureResponseFactory());
	}

	public static AccountResponseFactory anAccountResponseFactory() {
		return new AccountResponseFactory(anUserResponseFactory());
	}

	public static OrderResponseFactory anOrderResponseFactory(AverageRatingRepository averageRatingRepository) {
		return new OrderResponseFactory(aProductResponseFactory(averageRatingRepository), anUserResponseFactory());
	}

	public static BillResponseFactory aBillResponseFactory(CustomerService customerService, AverageRatingRepository averageRatingRepository) {
		return new BillResponseFactory(customerService, anOrderResponseFactory(averageRatingRepository), anUserResponseFactory(), aCustomerResponseFactory());
	}

	public static InvoiceResponseFactory anInvoiceResponseFactory() {
		return new InvoiceResponseFactory(anUserResponseFactory(), aCustomerResponseFactory());
	}

	public static ProductResponseFactory aProductResponseFactory(AverageRatingRepository averageRatingRepository) {
		return new ProductResponseFactory(aPictureResponseFactory(), averageRatingRepository);
	}

	public static TagResponseFactory aTagResponseFactory() {
		return new TagResponseFactory(aCustomerResponseFactory());
	}

}
