package com.ovvium.integration.application;

import com.ovvium.services.model.product.Product;
import com.ovvium.services.repository.ProductRepository;
import com.ovvium.services.web.controller.bff.v1.transfer.response.product.ProductGroupResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.product.ProductItemResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.ovvium.integration.DbDataConstants.*;
import static com.ovvium.services.model.bill.ServiceBuilderLocation.KITCHEN;
import static com.ovvium.services.model.product.ProductType.FOOD;
import static com.ovvium.utils.SpringMockMvcUtils.doGet;
import static com.ovvium.utils.SpringMockMvcUtils.doPost;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProductApiControllerIT extends AbstractApplicationIntegrationTest {

	@Autowired
	private ProductRepository productRepository;

	@Test
	public void given_persisted_product_group_when_get_product_group_by_id_then_should_return_response_correctly() {
		final String accessToken = loginUser(USER_2_EMAIL, USER_2_PASSWORD);

		final ProductGroupResponse groupResponse = doGet(mockMvc, format("/customers/%s/products/%s", CUSTOMER_1_ID, PRODUCT_2_GROUP_ID), accessToken, ProductGroupResponse.class);

		assertThat(groupResponse.getId()).isEqualTo(PRODUCT_2_GROUP_ID);
		assertThat(groupResponse.getStartTime()).isEqualTo("00:00");
		assertThat(groupResponse.getEndTime()).isEqualTo("23:59:59");
		assertThat(groupResponse.getProductType()).isEqualTo("PRODUCT_GROUP");
		assertThat(groupResponse.getProducts()).isNotEmpty();
	}

	@Test
	public void given_persisted_product_group_when_get_product_group_by_id_cached_then_should_return_response_correctly() {
		final String accessToken = loginUser(USER_2_EMAIL, USER_2_PASSWORD);

		String uri = format("/customers/%s/products/%s", CUSTOMER_1_ID, PRODUCT_2_GROUP_ID);
		final ProductGroupResponse groupResponse = doGet(mockMvc, uri, accessToken, ProductGroupResponse.class);
		final ProductGroupResponse cachedResponse = doGet(mockMvc, uri, accessToken, ProductGroupResponse.class);

		assertThat(groupResponse.getId()).isEqualTo(PRODUCT_2_GROUP_ID);
		assertThat(cachedResponse.getId()).isEqualTo(PRODUCT_2_GROUP_ID);
	}

	@Test
	public void given_persisted_product_item_and_cover_and_pictures_when_get_product_item_by_id_then_should_return_all_pictures() {
		final String accessToken = loginUser(USER_2_EMAIL, USER_2_PASSWORD);

		final ProductItemResponse response = doGet(mockMvc, format("/customers/%s/products/%s", CUSTOMER_1_ID, PRODUCT_1_ID), accessToken, ProductItemResponse.class);

		assertThat(response.getId()).isEqualTo(PRODUCT_1_ID);
		assertThat(response.getPictures()).isNotEmpty();

		transactionalHelper.executeWithinTransaction(() -> {
			final Product product = productRepository.getOrFail(PRODUCT_1_ID);
			int picturesSize = product.getPictures().size();
			if (product.getCoverPicture().isPresent()) {
				picturesSize++;
			}
			;
			assertThat(response.getPictures()).hasSize(picturesSize);
		});
	}

	@Test
	public void given_create_product_item_json_when_create_product_item_then_should_create_product_correctly() {
		final String accessToken = loginUser(CUSTOMER_2_USER_1_EMAIL, CUSTOMER_2_USER_1_PASSWORD);

		final ProductItemResponse response = doPost(mockMvc,
				format("/customers/%s/products/product-items", CUSTOMER_2_ID),
				fromJson("create_product_item.json"),
				status().isCreated(),
				accessToken,
				ProductItemResponse.class);

		assertThat(response.getType()).isEqualTo(FOOD);
		assertThat(response.getServiceBuilderLocation()).isEqualTo(KITCHEN);
		assertThat(response.getName().getDefaultValue()).isEqualTo("Huevos fritos");
		assertThat(response.getDescription().getDefaultValue()).isEqualTo("a la sartén");
		assertThat(response.getBasePrice()).isEqualTo(3.5d);
		assertThat(response.getTax()).isEqualTo(0.21d);
	}


}
