package com.ovvium.services.transfer.command.product;

import com.ovvium.services.model.bill.ServiceBuilderLocation;
import com.ovvium.services.model.common.MoneyAmount;
import com.ovvium.services.model.common.MultiLangString;
import com.ovvium.services.model.product.Category;
import com.ovvium.services.model.product.Picture;
import com.ovvium.services.model.product.Product;
import com.ovvium.services.model.product.ProductOptionGroup;
import com.ovvium.services.model.user.Allergen;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public class UpdateProductCommand {

    private final Product product;
    private final Category category;
    private final MultiLangString name;
    private final MultiLangString description;
    private final ServiceBuilderLocation serviceBuilderLocation;
    private final MoneyAmount basePrice;
    private final Double tax;
    private final Set<Allergen> allergens;
    private final Picture coverPicture;
    private final Integer order;
    private final Boolean hidden;
    private final Boolean recommended;
    private final List<ProductOptionGroup> options;

    public Optional<Category> getCategory() {
        return Optional.ofNullable(category);
    }

    public Optional<MultiLangString> getDescription() {
        return Optional.ofNullable(description);
    }

    public Optional<ServiceBuilderLocation> getServiceBuilderLocation() {
        return Optional.ofNullable(serviceBuilderLocation);
    }

    public Optional<MoneyAmount> getBasePrice() {
        return Optional.ofNullable(basePrice);
    }

    public Optional<Double> getTax() {
        return Optional.ofNullable(tax);
    }

    public Optional<Set<Allergen>> getAllergens() {
        return Optional.ofNullable(allergens);
    }

    public Optional<Picture> getCoverPicture() {
        return Optional.ofNullable(coverPicture);
    }

    public Optional<Integer> getOrder() {
        return Optional.ofNullable(order);
    }

    public Optional<Boolean> getHidden() {
        return Optional.ofNullable(hidden);
    }

    public Optional<Boolean> getRecommended() {
        return Optional.ofNullable(recommended);
    }

    public Optional<List<ProductOptionGroup>> getOptions() {
        return Optional.ofNullable(options);
    }

    public Optional<MultiLangString> getName() {
        return Optional.ofNullable(name);
    }
}
