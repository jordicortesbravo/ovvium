package com.ovvium.services.transfer.command.category;

import com.ovvium.services.model.common.MultiLangString;
import com.ovvium.services.model.customer.Customer;

public record CreateCategoryCommand(
        Customer customer,
        MultiLangString title
) {
}
