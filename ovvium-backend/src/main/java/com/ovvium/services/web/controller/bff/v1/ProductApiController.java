package com.ovvium.services.web.controller.bff.v1;

import com.ovvium.services.service.application.ProductApplicationService;
import com.ovvium.services.web.controller.bff.v1.transfer.request.category.CreateCategoryRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.category.UpdateCategoryRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.product.*;
import com.ovvium.services.web.controller.bff.v1.transfer.response.category.CategoryResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.product.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.ovvium.services.web.controller.bff.v1.WebConstants.BASE_URI_API_V1;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = BASE_URI_API_V1, produces = APPLICATION_JSON_VALUE)
public class ProductApiController {

	private final ProductApplicationService applicationService;

	@ResponseStatus(OK)
	@GetMapping("/customers/{customerId}/categories")
	public List<CategoryResponse> listCategories(@PathVariable UUID customerId) {
		return applicationService.listCategories(customerId).toList();
	}

	@ResponseStatus(CREATED)
	@PostMapping("/customers/{customerId}/categories")
	@PreAuthorize("(hasRole('CUSTOMERS_ADMIN') and @auth.isFromCustomer(#customerId)) or hasRole('OVVIUM_ADMIN')")
	public CategoryResponse createCategory(@PathVariable UUID customerId, @RequestBody CreateCategoryRequest request) {
		return applicationService.createCategory(customerId, request);
	}

	@ResponseStatus(OK)
	@GetMapping("/customers/{customerId}/categories/{categoryId}")
	@PreAuthorize("(hasRole('CUSTOMERS_ADMIN') and @auth.isFromCustomer(#customerId)) or hasRole('OVVIUM_ADMIN')")
	public CategoryResponse getCategory(@PathVariable UUID customerId, @PathVariable UUID categoryId) {
		return applicationService.getCategory(categoryId);
	}

	@ResponseStatus(OK)
	@PatchMapping("/customers/{customerId}/categories/{categoryId}")
	@PreAuthorize("(hasRole('CUSTOMERS_ADMIN') and @auth.isFromCustomer(#customerId)) or hasRole('OVVIUM_ADMIN')")
	public CategoryResponse updateCategory(@PathVariable UUID customerId, @PathVariable UUID categoryId, @RequestBody UpdateCategoryRequest request) {
		return applicationService.updateCategory(customerId, categoryId, request);
	}

	@ResponseStatus(CREATED)
	@PostMapping("/customers/{customerId}/products/product-items")
	@PreAuthorize("(hasRole('CUSTOMERS_ADMIN') and @auth.isFromCustomer(#customerId)) or hasRole('OVVIUM_ADMIN')")
	public ProductResponse createProductItem(@PathVariable UUID customerId, @RequestBody CreateProductItemRequest request) {
		return applicationService.createProductItem(customerId, request);
	}

	@ResponseStatus(CREATED)
	@PostMapping("/customers/{customerId}/products/product-groups")
	@PreAuthorize("(hasRole('CUSTOMERS_ADMIN') and @auth.isFromCustomer(#customerId)) or hasRole('OVVIUM_ADMIN')")
	public ProductResponse createProductGroup(@PathVariable UUID customerId, @RequestBody CreateProductGroupRequest request) {
		return applicationService.createProductGroup(customerId, request);
	}

	@ResponseStatus(OK)
	@PatchMapping("/customers/{customerId}/products/{productId}")
	@PreAuthorize("(hasRole('CUSTOMERS_ADMIN') and @auth.isFromCustomer(#customerId)) or hasRole('OVVIUM_ADMIN')")
	public ProductResponse updateProduct(@PathVariable UUID customerId,
											@PathVariable UUID productId,
											@RequestBody UpdateProductRequest request) {
		return applicationService.updateProduct(productId, request);
	}

	@ResponseStatus(OK)
	@PatchMapping("/customers/{customerId}/products/product-items/{productId}")
	@PreAuthorize("(hasRole('CUSTOMERS_ADMIN') and @auth.isFromCustomer(#customerId)) or hasRole('OVVIUM_ADMIN')")
	public ProductResponse updateProductItem(@PathVariable UUID customerId,
											@PathVariable UUID productId,
											@RequestBody UpdateProductItemRequest request) {
		return applicationService.updateProductItem(productId, request);
	}

	@ResponseStatus(OK)
	@PatchMapping("/customers/{customerId}/products/product-groups/{productId}")
	@PreAuthorize("(hasRole('CUSTOMERS_ADMIN') and @auth.isFromCustomer(#customerId)) or hasRole('OVVIUM_ADMIN')")
	public ProductResponse updateProduct(@PathVariable UUID customerId,
											@PathVariable UUID productId,
											@RequestBody UpdateProductGroupRequest request) {
		return applicationService.updateProductGroup(productId, request);
	}

	@ResponseStatus(OK)
	@GetMapping("/customers/{customerId}/products")
	public List<ProductResponse> listProducts(@PathVariable UUID customerId) {
		return applicationService.listProducts(customerId).toList();
	}

	@ResponseStatus(OK)
	@GetMapping("/customers/{customerId}/products/{productId}")
	public ProductResponse getProduct(@PathVariable String customerId, @PathVariable UUID productId) {
		return applicationService.getProduct(productId);
	}

	@ResponseStatus(OK)
	@DeleteMapping("/customers/{customerId}/products/{productId}")
	@PreAuthorize("(hasRole('CUSTOMERS_ADMIN') and @auth.isFromCustomer(#customerId)) or hasRole('OVVIUM_ADMIN')")
	public void removeProduct( @PathVariable String customerId, @PathVariable UUID productId) {
		applicationService.remove(productId);
	}

	@ResponseStatus(OK)
	@PostMapping(value = "/customers/{customerId}/products/{productId}/pictures")
	public ProductResponse addPictureToProduct(@PathVariable UUID customerId,
											   @PathVariable UUID productId,
											   @RequestBody CreateProductPictureRequest request) {
		return applicationService.addPictureToProduct(productId, request);
	}
}
