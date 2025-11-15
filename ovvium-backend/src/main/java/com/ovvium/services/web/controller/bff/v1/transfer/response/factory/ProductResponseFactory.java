package com.ovvium.services.web.controller.bff.v1.transfer.response.factory;

import com.ovvium.services.model.product.Product;
import com.ovvium.services.model.product.ProductGroup;
import com.ovvium.services.model.rating.AverageRating;
import com.ovvium.services.repository.AverageRatingRepository;
import com.ovvium.services.util.util.basic.Utils;
import com.ovvium.services.web.controller.bff.v1.transfer.response.picture.PictureResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.product.*;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static com.ovvium.services.model.product.ProductType.FOOD;
import static java.util.Map.Entry.comparingByKey;


@Component
@RequiredArgsConstructor
public class ProductResponseFactory {

	private final PictureResponseFactory pictureResponseFactory;
	private final AverageRatingRepository averageRatingRepository;

	public ProductResponse create(Product product) {
		val rating = averageRatingRepository.get(product.getId()).orElse(null);
		val pictures = createPictureResponses(product);
		if (product instanceof ProductGroup) {
			val groupPictures = pictures.isEmpty() ? createGroupPictureResponses(product.as(ProductGroup.class)) : pictures;
			return createProductGroupResponse(product, groupPictures, rating);
		}
		return new ProductItemResponse(product, pictures, rating);
	}

	public ProductResponse createSimple(Product product) {
		val rating = averageRatingRepository.get(product.getId()).orElse(null);
		if (product instanceof ProductGroup) {
			val coverPicture = createCoverPictureResponse(product)
					.orElseGet(() -> createGroupCoverPictureResponse(product.as(ProductGroup.class)));
			return new ProductGroupSimpleResponse(product.as(ProductGroup.class), coverPicture, rating);
		}
		return new ProductItemSimpleResponse(product, createCoverPictureResponse(product).orElse(null), rating);
	}

	private ProductResponse createProductGroupResponse(Product product, List<Map<String, PictureResponse>> pictures, AverageRating rating) {
		val productGroup = (ProductGroup) product;
		val productResponses = productGroup.getProducts()
				.entrySet().stream()
				.sorted(comparingByKey())
				.collect(Collectors.toMap(
						Map.Entry::getKey,
						v -> v.getValue().stream().map(this::create).collect(Collectors.toList()),
						(v1, v2) -> v1,
						LinkedHashMap::new
				));
		return new ProductGroupResponse(productGroup, pictures, rating, productResponses);
	}

	private List<Map<String, PictureResponse>> createPictureResponses(Product product) {
		val pictures = new LinkedList<>(product.getPictures());
		product.getCoverPicture().ifPresent(pictures::addFirst);
		return pictures.stream()
				.map(pictureResponseFactory::getCropsResponses)
				.collect(Collectors.toList());
	}

	private Optional<Map<String, PictureResponse>> createCoverPictureResponse(Product product) {
		return product.getCoverPicture()
				.map(pictureResponseFactory::getCropsResponses);
	}

	private Map<String, PictureResponse> createGroupCoverPictureResponse(ProductGroup productGroup) {
		return productGroup.getProducts().values().stream()
				.flatMap(Set::stream)
				.filter(it -> it.getCoverPicture().isPresent())
				.sorted(Comparator.comparing(Product::getType, (p1, p2) -> p1 == FOOD ? -1 : 0))
				.findAny()
				.flatMap(this::createCoverPictureResponse)
				.orElse(null);
	}

	// Get first product of each ServiceTime on the Group and create picture responses
	private List<Map<String, PictureResponse>> createGroupPictureResponses(ProductGroup product) {
		return product.getProducts().values().stream()
				.map(Utils::first)
				.map(this::createPictureResponses)
				.flatMap(List::stream)
				.collect(Collectors.toList());
	}

}
