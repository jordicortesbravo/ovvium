package com.ovvium.services.transfer.command.product;

import com.ovvium.services.model.bill.ServiceBuilderLocation;
import com.ovvium.services.model.bill.ServiceTime;
import com.ovvium.services.model.common.MoneyAmount;
import com.ovvium.services.model.common.MultiLangString;
import com.ovvium.services.model.product.*;
import com.ovvium.services.model.user.Allergen;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class UpdateProductGroupCommand extends UpdateProductCommand {

    private final Set<DayOfWeek> daysOfWeek;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final Map<ServiceTime, Set<ProductItem>> products;

    public UpdateProductGroupCommand(Product product,
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
                                     Set<DayOfWeek> daysOfWeek,
                                     LocalTime startTime,
                                     LocalTime endTime,
                                     Map<ServiceTime, Set<ProductItem>> products,
                                     List<ProductOptionGroup> options) {
        super(product, category, name, description, serviceBuilderLocation, basePrice, tax, allergens, coverPicture, order, hidden, recommended, options);
        this.daysOfWeek = daysOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.products = products;
    }


    public Optional<Set<DayOfWeek>> getDaysOfWeek() {
        return Optional.ofNullable(daysOfWeek);
    }

    public Optional<LocalTime> getStartTime() {
        return Optional.ofNullable(startTime);
    }

    public Optional<LocalTime> getEndTime() {
        return Optional.ofNullable(endTime);
    }

    public Optional<Map<ServiceTime, Set<ProductItem>>> getProducts() {
        return Optional.ofNullable(products);
    }
}


