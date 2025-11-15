package com.ovvium.services.transfer.command.product;

import com.ovvium.services.model.bill.ServiceBuilderLocation;
import com.ovvium.services.model.common.MoneyAmount;
import com.ovvium.services.model.common.MultiLangString;
import com.ovvium.services.model.product.*;
import com.ovvium.services.model.user.Allergen;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public final class UpdateProductItemCommand extends UpdateProductCommand {

    private final ProductType type;

    public UpdateProductItemCommand(Product product,
                                    Category category,
                                    MultiLangString name,
                                    MultiLangString description,
                                    ServiceBuilderLocation serviceBuilderLocation,
                                    MoneyAmount basePrice,
                                    Double tax,
                                    Set<Allergen> allergens,
                                    Picture coverPicture,
                                    Integer order,
                                    Boolean hidden,
                                    Boolean recommended,
                                    ProductType type,
                                    List<ProductOptionGroup> options) {
        super(product, category, name, description, serviceBuilderLocation, basePrice, tax, allergens, coverPicture, order, hidden, recommended, options);
        this.type = type;
    }

    public Optional<ProductType> getType() {
        return Optional.ofNullable(type);
    }
}
