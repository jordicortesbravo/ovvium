package com.ovvium.services.util.common.domain.adapters;

import lombok.val;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.ovvium.services.util.common.domain.PageRequest;
import com.ovvium.services.util.common.domain.Request;
import com.ovvium.services.util.common.domain.transfers.PageRequestTransfer;

public class
PageRequestAdapter extends XmlAdapter<PageRequestTransfer, PageRequest> {

    @Override
    public PageRequest unmarshal(PageRequestTransfer value) {
        return value == null ? null : Request //
                .of(value.getFilters(), value.getOrders()) //
                .distinct(value.isDistinct()) //
                .page(value.getPageNumber(), value.getPageSize());
    }

    @Override
    public PageRequestTransfer marshal(PageRequest bound) {

        if (bound == null) {
            return null;
        }

        val value = new PageRequestTransfer();
        value.setFilters(bound.getFilters());
        value.setOrders(bound.getOrders());
        value.setDistinct(bound.isDistinct());
        value.setPageNumber(bound.getPageNumber());
        value.setPageSize(bound.getPageSize());

        return value;
    }

}
