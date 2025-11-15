package com.ovvium.services.util.common.domain.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.ovvium.services.util.common.domain.Pageable;
import com.ovvium.services.util.common.domain.transfers.PageableTransfer;

public class PageableAdapter extends XmlAdapter<PageableTransfer, Pageable>{

    @Override
    public Pageable unmarshal(PageableTransfer pageable) {
        if(pageable == null) {
            return null;
        }
        
        return new Pageable(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
    }

    @Override
    public PageableTransfer marshal(Pageable pageable) {
        if(pageable == null) {
            return null;
        }
        return new PageableTransfer(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
    }
}
