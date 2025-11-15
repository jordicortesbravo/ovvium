package com.ovvium.utils;

import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import com.ovvium.services.util.util.xson.Xson;
import com.ovvium.services.util.util.xson.XsonFactory;
import com.ovvium.services.util.util.xson.XsonFactoryConfigurer;
import com.ovvium.services.web.controller.bff.v1.transfer.response.product.ProductGroupResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.product.ProductItemResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.product.ProductResponse;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.nio.charset.StandardCharsets;

import static com.ovvium.integration.DbDataConstants.API_KEY_TESTS;
import static com.ovvium.services.security.ApiKeyAuthenticationFilter.API_KEY_HEADER;
import static com.ovvium.services.web.controller.bff.v1.WebConstants.BASE_URI_API_V1;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public final class SpringMockMvcUtils {

    public static final XsonFactory xsonFactory = configureXson().build();

    @SneakyThrows
    public static <T> T doPost(MockMvc mockMvc, String url, Object request, ResultMatcher expect, String accessToken, Class<T> clazz) {
        MockHttpServletRequestBuilder builder = post(BASE_URI_API_V1 + url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request instanceof String ? (String) request : xsonFactory.of(request).toString());
        if (accessToken != null) {
            builder.header("Authorization", "Bearer " + accessToken);
        }
        builder.header(API_KEY_HEADER, API_KEY_TESTS);
        return getAsObject(mockMvc,builder, expect, clazz);
    }

    @SneakyThrows
    public static <T> T doGet(MockMvc mockMvc, String url, String accessToken, Class<T> clazz) {
        MockHttpServletRequestBuilder builder = get(BASE_URI_API_V1 + url)
                .contentType(MediaType.APPLICATION_JSON);
        if (accessToken != null) {
            builder.header("Authorization", "Bearer " + accessToken);
        }
        builder.header(API_KEY_HEADER, API_KEY_TESTS);
        return xsonFactory.create(mockMvc.perform(builder)
                .andReturn()
                .getResponse()
                .getContentAsString())
                .as(clazz);
    }

    @SneakyThrows
    public static <T> T doPatch(MockMvc mockMvc, String url, Object request, ResultMatcher expect, String accessToken, Class<T> clazz) {
        MockHttpServletRequestBuilder builder = patch(BASE_URI_API_V1 + url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(xsonFactory.of(request).toString());
        if (accessToken != null) {
            builder.header("Authorization", "Bearer " + accessToken);
        }
        builder.header(API_KEY_HEADER, API_KEY_TESTS);
        return getAsObject(mockMvc, builder, expect, clazz);
    }

    @SneakyThrows
    public static <T> T doDelete(MockMvc mockMvc, String url, ResultMatcher expect, String accessToken, Class<T> clazz) {
        MockHttpServletRequestBuilder builder = delete(BASE_URI_API_V1 + url)
                .contentType(MediaType.APPLICATION_JSON);
        if (accessToken != null) {
            builder.header("Authorization", "Bearer " + accessToken);
        }
        builder.header(API_KEY_HEADER, API_KEY_TESTS);
        return getAsObject(mockMvc, builder, expect, clazz);
    }

    private static <T> T getAsObject(MockMvc mockMvc, MockHttpServletRequestBuilder builder, ResultMatcher expect, Class<T> clazz) throws Exception {
        return xsonFactory.create(mockMvc.perform(builder)
                .andExpect(expect)
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8))
                .as(clazz);
    }

    private static XsonFactoryConfigurer configureXson() {
        RuntimeTypeAdapterFactory<ProductResponse> productAdapterFactory = RuntimeTypeAdapterFactory.of(ProductResponse.class, "productType")
                .registerSubtype(ProductItemResponse.class, "PRODUCT_ITEM")
                .registerSubtype(ProductGroupResponse.class, "PRODUCT_GROUP");
        return Xson.configurer().registerTypeAdapterFactory(productAdapterFactory);
    }

}
