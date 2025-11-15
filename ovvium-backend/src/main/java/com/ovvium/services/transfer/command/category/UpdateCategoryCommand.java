package com.ovvium.services.transfer.command.category;

import com.ovvium.services.model.common.MultiLangString;
import com.ovvium.services.model.product.Category;

import java.util.Optional;

public record UpdateCategoryCommand(
        Category category,
        MultiLangString title,
        Integer order
) {

    public Optional<Integer> getOrder() {
        return Optional.ofNullable(order);
    }

    public Optional<MultiLangString> getTitle() {
        return Optional.ofNullable(title);
    }
}
