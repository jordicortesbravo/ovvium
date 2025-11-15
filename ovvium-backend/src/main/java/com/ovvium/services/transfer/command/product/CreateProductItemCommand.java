package com.ovvium.services.transfer.command.product;

import com.ovvium.services.model.bill.ServiceBuilderLocation;
import com.ovvium.services.model.common.MoneyAmount;
import com.ovvium.services.model.common.MultiLangString;
import com.ovvium.services.model.customer.Customer;
import com.ovvium.services.model.product.Category;
import com.ovvium.services.model.product.Picture;
import com.ovvium.services.model.product.ProductOptionGroup;
import com.ovvium.services.model.product.ProductType;
import com.ovvium.services.model.user.Allergen;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public record CreateProductItemCommand(
        Customer customer,
        Category category,
        MultiLangString name,
        MultiLangString description,
        ProductType productType,
        ServiceBuilderLocation serviceBuilderLocation,
        MoneyAmount basePrice,
        double tax,
        Set<Allergen> allergens,
        Picture coverPicture,
        List<ProductOptionGroup> options
) {
    public Optional<Picture> getCoverPicture() {
        return Optional.ofNullable(coverPicture);
    }

    public Optional<List<ProductOptionGroup>> getOptions() {
        return Optional.ofNullable(options);
    }

    public Optional<MultiLangString> getDescription() {
        return Optional.ofNullable(description);
    }

}
