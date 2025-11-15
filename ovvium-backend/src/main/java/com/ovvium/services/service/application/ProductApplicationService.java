package com.ovvium.services.service.application;

import com.ovvium.services.model.bill.ServiceTime;
import com.ovvium.services.model.common.MoneyAmount;
import com.ovvium.services.model.common.MultiLangString;
import com.ovvium.services.model.product.ProductItem;
import com.ovvium.services.model.product.ProductOption;
import com.ovvium.services.model.product.ProductOptionGroup;
import com.ovvium.services.model.product.ProductType;
import com.ovvium.services.service.CustomerService;
import com.ovvium.services.service.PictureService;
import com.ovvium.services.service.ProductService;
import com.ovvium.services.transfer.command.category.CreateCategoryCommand;
import com.ovvium.services.transfer.command.category.UpdateCategoryCommand;
import com.ovvium.services.transfer.command.product.*;
import com.ovvium.services.transfer.request.common.MultiLangStringRequest;
import com.ovvium.services.util.ovvium.base.Validations;
import com.ovvium.services.util.ovvium.cache.CollectionWrapper;
import com.ovvium.services.web.controller.bff.v1.transfer.request.category.CreateCategoryRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.category.UpdateCategoryRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.product.*;
import com.ovvium.services.web.controller.bff.v1.transfer.response.category.CategoryResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.factory.ProductResponseFactory;
import com.ovvium.services.web.controller.bff.v1.transfer.response.product.ProductResponse;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.ovvium.services.app.constant.Caches.*;
import static com.ovvium.services.model.common.MultiLangString.ofDefaultAndTranslations;
import static java.util.stream.Collectors.toList;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductApplicationService {

    private final ProductResponseFactory responseFactory;
    private final ProductService service;
    private final CustomerService customerService;
    private final PictureService pictureService;

    @Cacheable(value = CATEGORIES_BY_CUSTOMER)
    public CollectionWrapper<CategoryResponse> listCategories(UUID customerId) {
        return CollectionWrapper.of(customerId,
                service.listCategories(customerId).stream()
                        .map(CategoryResponse::new)
                        .collect(Collectors.toList())
        );
    }

    @Cacheable(value = CATEGORIES)
    public CategoryResponse getCategory(UUID categoryId) {
        return new CategoryResponse(service.getCategory(categoryId));
    }

    @Caching(put = @CachePut(value = CATEGORIES, key = "#result.id"),
            evict = @CacheEvict(value = CATEGORIES_BY_CUSTOMER, key = "#customerId"))
    public CategoryResponse createCategory(UUID customerId, CreateCategoryRequest request) {
        Validations.validate(request);
        val customer = customerService.getCustomer(customerId);
        val command = new CreateCategoryCommand(customer,
                toMultiLangString(request.getName())
        );
        return new CategoryResponse(service.createCategory(command));
    }

    @Caching(put = @CachePut(value = CATEGORIES, key = "#result.id"),
            evict = {@CacheEvict(value = CATEGORIES_BY_CUSTOMER, key = "#customerId"),
                    @CacheEvict(value = PRODUCTS_BY_CUSTOMER, key = "#customerId")})
    public CategoryResponse updateCategory(UUID customerId, UUID categoryId, UpdateCategoryRequest request) {
        Validations.validate(request);
        val category = service.getCategory(categoryId);
        return new CategoryResponse(
                service.updateCategory(new UpdateCategoryCommand(
                        category,
                        request.getName()
                                .map(this::toMultiLangString)
                                .orElse(null),
                        request.getOrder().orElse(null)
                ))
        );
    }

    @Cacheable(value = PRODUCTS_BY_CUSTOMER)
    public CollectionWrapper<ProductResponse> listProducts(UUID customerId) {
        return CollectionWrapper.of(
                customerId,
                service.listProducts(customerId)
                        .stream()
                        .map(responseFactory::createSimple)
                        .collect(Collectors.toList())
        );
    }

    @Caching(put = @CachePut(value = PRODUCTS, key = "#result.id"),
            evict = {@CacheEvict(value = PRODUCTS_BY_CUSTOMER, key = "#customerId")})
    public ProductResponse createProductItem(UUID customerId, CreateProductItemRequest request) {
        Validations.validate(request);
        val category = service.getCategory(request.getCategoryId());
        val customer = customerService.getCustomer(customerId);
        val product = service.create(new CreateProductItemCommand(
                customer,
                category,
                toMultiLangString(request.getName()),
                request.getDescription().map(this::toMultiLangString).orElse(null),
                ProductType.valueOf(request.getType()),
                request.getServiceBuilderLocation(),
                MoneyAmount.ofDouble(request.getBasePrice()),
                request.getTax(),
                request.getAllergens(),
                request.getCoverPictureId().map(pictureService::getOrFail).orElse(null),
                request.getOptions().map(this::mapProductOptionGroup).orElse(null)
        ));
        return responseFactory.create(product);
    }

    @Caching(put = @CachePut(value = PRODUCTS, key = "#result.id"),
            evict = {@CacheEvict(value = PRODUCTS_BY_CUSTOMER, key = "#customerId")})
    public ProductResponse createProductGroup(UUID customerId, CreateProductGroupRequest request) {
        Validations.validate(request);
        val category = service.getCategory(request.getCategoryId());
        val customer = customerService.getCustomer(customerId);
        val product = service.createGroup(new CreateProductGroupCommand(
                customer,
                category,
                toMultiLangString(request.getName()),
                request.getDescription().map(this::toMultiLangString).orElse(null),
                request.getServiceBuilderLocation(),
                MoneyAmount.ofDouble(request.getBasePrice()),
                request.getTax(),
                request.getAllergens(),
                request.getCoverPictureId().map(pictureService::getOrFail).orElse(null),
                request.getDaysOfWeek(),
                request.getStartTime().orElse(null),
                request.getStartTime().orElse(null),
                mapToProductItemsMap(request.getProductIds()),
                request.getOptions().map(this::mapProductOptionGroup).orElse(null)
        ));
        return responseFactory.create(product);
    }

    @Caching(put = @CachePut(value = PRODUCTS, key = "#result.id"),
            evict = {@CacheEvict(value = PRODUCTS_BY_CUSTOMER, key = "#result.customerId")})
    public ProductResponse updateProduct(UUID productId, UpdateProductRequest request) {
        val product = service.update(new UpdateProductCommand(
                service.getProduct(productId),
                request.getCategoryId().map(service::getCategory).orElse(null),
                request.getName().map(this::toMultiLangString).orElse(null),
                request.getDescription().map(this::toMultiLangString).orElse(null),
                request.getServiceBuilderLocation().orElse(null),
                request.getBasePrice().map(MoneyAmount::ofDouble).orElse(null),
                request.getTax().orElse(null),
                request.getAllergens().orElse(null),
                request.getCoverPictureId().map(pictureService::getOrFail).orElse(null),
                request.getOrder().orElse(null),
                request.getHidden().orElse(null),
                request.getRecommended().orElse(null),
                request.getOptions().map(this::mapProductOptionGroup).orElse(null)
        ));
        return responseFactory.create(product);
    }

    @Caching(put = @CachePut(value = PRODUCTS, key = "#result.id"),
            evict = {@CacheEvict(value = PRODUCTS_BY_CUSTOMER, key = "#result.customerId")})
    public ProductResponse updateProductGroup(UUID productId, UpdateProductGroupRequest request) {
        val product = service.updateGroup(new UpdateProductGroupCommand(
                service.getProduct(productId),
                request.getCategoryId().map(service::getCategory).orElse(null),
                request.getName().map(this::toMultiLangString).orElse(null),
                request.getDescription().map(this::toMultiLangString).orElse(null),
                request.getServiceBuilderLocation().orElse(null),
                request.getBasePrice().map(MoneyAmount::ofDouble).orElse(null),
                request.getTax().orElse(null),
                request.getAllergens().orElse(null),
                request.getCoverPictureId().map(pictureService::getOrFail).orElse(null),
                request.getOrder().orElse(null),
                request.getHidden().orElse(null),
                request.getRecommended().orElse(null),
                request.getDaysOfWeek().orElse(null),
                request.getStartTime().orElse(null),
                request.getEndTime().orElse(null),
                request.getProductIds().map(this::mapToProductItemsMap).orElse(null),
                request.getOptions().map(this::mapProductOptionGroup).orElse(null))
        );
        return responseFactory.create(product);
    }

    @Caching(put = @CachePut(value = PRODUCTS, key = "#result.id"),
            evict = {@CacheEvict(value = PRODUCTS_BY_CUSTOMER, key = "#result.customerId")})
    public ProductResponse updateProductItem(UUID productId, UpdateProductItemRequest request) {
        val product = service.updateItem(new UpdateProductItemCommand(
                service.getProduct(productId),
                request.getCategoryId().map(service::getCategory).orElse(null),
                request.getName().map(this::toMultiLangString).orElse(null),
                request.getDescription().map(this::toMultiLangString).orElse(null),
                request.getServiceBuilderLocation().orElse(null),
                request.getBasePrice().map(MoneyAmount::ofDouble).orElse(null),
                request.getTax().orElse(null),
                request.getAllergens().orElse(null),
                request.getCoverPictureId().map(pictureService::getOrFail).orElse(null),
                request.getOrder().orElse(null),
                request.getHidden().orElse(null),
                request.getRecommended().orElse(null),
                request.getType().orElse(null),
                request.getOptions().map(this::mapProductOptionGroup).orElse(null))
        );
        return responseFactory.create(product);
    }

    @CacheEvict(PRODUCTS)
    public void remove(UUID productId) {
        service.remove(productId);
    }

    @CachePut(value = PRODUCTS, key = "#result.id")
    public ProductResponse addPictureToProduct(UUID productId, CreateProductPictureRequest request) {
        Validations.validate(request);
        val product = service.getProduct(productId);
        val picture = pictureService.getOrFail(request.getPictureId());
        return responseFactory.create(service.addPictureToProduct(product, picture));
    }

    @Cacheable(PRODUCTS)
    public ProductResponse getProduct(UUID productId) {
        return responseFactory.create(service.getProduct(productId));
    }

    private Map<ServiceTime, Set<ProductItem>> mapToProductItemsMap(Map<ServiceTime, Set<UUID>> productIds) {
        return productIds.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, it -> it.getValue().stream()
                        .map(service::getProduct)
                        .map(p -> p.as(ProductItem.class)).collect(Collectors.toSet())));
    }

    private List<ProductOptionGroup> mapProductOptionGroup(List<ProductItemOptionGroupRequest> requests) {
        return requests.stream().map(this::createProductOptionGroup).collect(toList());
    }

    private ProductOptionGroup createProductOptionGroup(ProductItemOptionGroupRequest request) {
        MultiLangStringRequest request1 = request.getTitle();
        return new ProductOptionGroup(
                toMultiLangString(request1),
                request.getType(),
                request.getChoices().stream().map(this::createProductOption).collect(toList()),
                request.getRequired()
        );
    }

    private ProductOption createProductOption(ProductItemOptionRequest request) {
        MultiLangStringRequest request1 = request.getTitle();
        return new ProductOption(toMultiLangString(request1), request.getBasePrice(), request.getTax());
    }

    private MultiLangString toMultiLangString(MultiLangStringRequest request) {
        return ofDefaultAndTranslations(request.getDefaultValue(), request.getTranslations());
    }

}
