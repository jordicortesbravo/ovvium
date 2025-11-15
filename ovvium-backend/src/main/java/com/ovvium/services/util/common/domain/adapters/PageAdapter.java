package com.ovvium.services.util.common.domain.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.ovvium.services.util.common.domain.Page;
import com.ovvium.services.util.common.domain.SimplePage;
import com.ovvium.services.util.common.domain.transfers.PageTransfer;


public class PageAdapter extends XmlAdapter<PageTransfer, Page<?>>{

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Page<?> unmarshal(PageTransfer pageTransfer) {
        return new SimplePage(pageTransfer.getContent(), pageTransfer.getPageNumber(), pageTransfer.getPageSize(), pageTransfer.getTotalElements());
    }

    @Override
    public PageTransfer marshal(Page<?> page) {
        return new PageTransfer(page.getContent(), page.getPageNumber(), page.getPageSize(), page.getTotalElements());
    }
}
