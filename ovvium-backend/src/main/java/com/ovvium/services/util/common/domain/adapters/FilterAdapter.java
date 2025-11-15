package com.ovvium.services.util.common.domain.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.ovvium.services.util.common.domain.Filter;
import com.ovvium.services.util.common.domain.transfers.FilterTransfer;

public class FilterAdapter extends XmlAdapter<FilterTransfer, Filter>{

    @Override
    public Filter unmarshal(FilterTransfer filterTransfer) {
        if(filterTransfer == null) {
            return null;
        }
        
        Object value = filterTransfer.getValue();
        if(value instanceof String && filterTransfer.isNot()) {
            value = "!" + value;
        }
        return new Filter(filterTransfer.getField(), value, filterTransfer.getCondition());
    }

    @Override
    public FilterTransfer marshal(Filter filter) {
        if(filter == null) {
            return null;
        }
        
        return new FilterTransfer(filter.getField(), filter.getValue(), filter.getCondition(), filter.isNot());
    }
}
