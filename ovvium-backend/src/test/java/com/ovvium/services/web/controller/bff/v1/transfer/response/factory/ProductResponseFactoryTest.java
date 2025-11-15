package com.ovvium.services.web.controller.bff.v1.transfer.response.factory;

import com.google.common.collect.Sets;
import com.ovvium.mother.builder.ProductGroupBuilder;
import com.ovvium.mother.builder.ProductItemBuilder;
import com.ovvium.mother.model.PictureMother;
import com.ovvium.mother.model.ProductMother;
import com.ovvium.mother.response.ResponseFactoryMother;
import com.ovvium.services.model.bill.ServiceTime;
import com.ovvium.services.model.product.*;
import com.ovvium.services.repository.AverageRatingRepository;
import com.ovvium.services.util.util.basic.Utils;
import com.ovvium.services.web.controller.bff.v1.transfer.response.picture.PictureResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.product.ProductGroupResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.product.ProductGroupSimpleResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.product.ProductItemResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.product.ProductResponse;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;

import static com.ovvium.services.model.bill.ServiceTime.SOONER;
import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class ProductResponseFactoryTest {

	// SUT
	private ProductResponseFactory productResponseFactory;

	@Before
	public void setUp() {
		productResponseFactory = new ProductResponseFactory(ResponseFactoryMother.aPictureResponseFactory(), mock(AverageRatingRepository.class));
	}


	@Test
	public void given_product_item_when_create_response_then_should_create_product_item_response_correctly() {
		ProductItem product = ProductMother.getPatatasBravasProduct();

		ProductResponse productResponse = productResponseFactory.create(product);

		assertThat(productResponse).isExactlyInstanceOf(ProductItemResponse.class);
		ProductItemResponse productItemResponse = (ProductItemResponse) productResponse;
		assertThat(productItemResponse.getCategoryId()).isEqualTo(product.getCategory().getId());
		assertThat(productItemResponse.getPictures()).hasSize(2);
		assertSamePictureCrops(Utils.first(productItemResponse.getPictures()), product.getCoverPicture().get());
		assertSamePictureCrops(productItemResponse.getPictures().get(1), product.getPictures().get(0));
	}

	@Test
	public void given_empty_pictures_product_group_when_create_response_then_should_create_product_group_response_with_products_pictures() {
		ProductItem product = ProductMother.getPatatasBravasProduct();
		ProductGroup emptyPicturesProduct = new ProductGroupBuilder()
				.setCoverPicture(null)
				.setUserPicture(null)
				.setEntries(Collections.singletonList(new ProductGroupEntry(ServiceTime.SOONER, singleton(product))))
				.build();

		ProductResponse productResponse = productResponseFactory.create(emptyPicturesProduct);

		assertThat(productResponse).isExactlyInstanceOf(ProductGroupResponse.class);
		ProductGroupResponse productGroupResponse = (ProductGroupResponse) productResponse;
		assertThat(productGroupResponse.getCategoryId()).isEqualTo(emptyPicturesProduct.getCategory().getId());
		assertThat(productGroupResponse.getPictures()).hasSize(2);
		assertSamePictureCrops(Utils.first(productGroupResponse.getPictures()), product.getCoverPicture().get());
		assertSamePictureCrops(productGroupResponse.getPictures().get(1), product.getPictures().get(0));
	}

	@Test
	public void given_empty_pictures_product_group_when_create_simple_response_then_should_create_product_group_response_with_products_pictures() {
		ProductItem foodProduct = new ProductItemBuilder()
				.setType(ProductType.FOOD)
				.setCoverPicture(PictureMother.anyPicture())
				.build();
		ProductItem drinkProduct = ProductMother.getCervezaProduct();
		ProductGroup emptyPicturesProduct = new ProductGroupBuilder()
				.setCoverPicture(null)
				.setUserPicture(null)
				.setEntries(Collections.singletonList(new ProductGroupEntry(SOONER, Sets.newHashSet(drinkProduct, foodProduct))))
				.build();

		ProductResponse productResponse = productResponseFactory.createSimple(emptyPicturesProduct);

		assertThat(productResponse).isExactlyInstanceOf(ProductGroupSimpleResponse.class);
		ProductGroupSimpleResponse productGroupResponse = (ProductGroupSimpleResponse) productResponse;
		assertThat(productGroupResponse.getCategoryId()).isEqualTo(emptyPicturesProduct.getCategory().getId());
		assertSamePictureCrops(productGroupResponse.getCoverPicture(), foodProduct.getCoverPicture().get());
	}

	private void assertSamePictureCrops(Map<String, PictureResponse> pictureResponseMap, Picture picture) {
		assertThat(pictureResponseMap.get("low").getUrl().toString()).contains(picture.getCrops().get(PictureSize.LOW).toString());
		assertThat(pictureResponseMap.get("medium").getUrl().toString()).contains(picture.getCrops().get(PictureSize.MEDIUM).toString());
		assertThat(pictureResponseMap.get("high").getUrl().toString()).contains(picture.getCrops().get(PictureSize.HIGH).toString());
	}
}