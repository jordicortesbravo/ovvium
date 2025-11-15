package com.ovvium.services.transfer.command.product;

import com.ovvium.services.model.bill.ServiceBuilderLocation;
import com.ovvium.services.model.bill.ServiceTime;
import com.ovvium.services.model.common.MoneyAmount;
import com.ovvium.services.model.common.MultiLangString;
import com.ovvium.services.model.customer.Customer;
import com.ovvium.services.model.product.Category;
import com.ovvium.services.model.product.Picture;
import com.ovvium.services.model.product.ProductItem;
import com.ovvium.services.model.product.ProductOptionGroup;
import com.ovvium.services.model.user.Allergen;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public record CreateProductGroupCommand(
        Customer customer,
        Category category,
        MultiLangString name,
        MultiLangString description,
        ServiceBuilderLocation serviceBuilderLocation,
        MoneyAmount basePrice,
        double tax,
        Set<Allergen> allergens,
        Picture coverPicture,
        Set<DayOfWeek> daysOfWeek,
        LocalTime startTime,
        LocalTime endTime,
        Map<ServiceTime, Set<ProductItem>> products,
        List<ProductOptionGroup> options
) {
    public Optional<Picture> getCoverPicture() {
        return Optional.ofNullable(coverPicture);
    }

    public Optional<LocalTime> getStartTime() {
        return Optional.ofNullable(startTime);
    }

    public Optional<LocalTime> getEndTime() {
        return Optional.ofNullable(endTime);
    }

    public Optional<List<ProductOptionGroup>> getOptions() {
        return Optional.ofNullable(options);
    }

    public Optional<MultiLangString> getDescription() {
        return Optional.ofNullable(description);
    }
}
