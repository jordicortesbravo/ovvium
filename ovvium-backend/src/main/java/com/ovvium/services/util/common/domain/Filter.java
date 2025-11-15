package com.ovvium.services.util.common.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.ovvium.services.util.common.domain.adapters.FilterAdapter;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@XmlJavaTypeAdapter(FilterAdapter.class)
public class Filter implements Serializable {

    private static final long serialVersionUID = -3584918858057711475L;

    public static final List<Filter> toList(Filter filter) {
        List<Filter> filters = new ArrayList<Filter>(1);
        filters.add(filter);
        return filters;
    }

    public static final List<Filter> toList(String field, Object value, Condition condition) {
        return toList(new Filter(field, value, condition));
    }

    public enum Condition {
        EQUALS, NOT_EQUALS, DATE_EQUALS, STARTS_WITH, ENDS_WITH, CONTAINS, CONTAINS_IGNORE_CASE, LT, LE, GT, GE, EMPTY, EMPTY_LIST, IN, NOT_IN, IS_NULL, IS_NOT_NULL, BETWEEN, MEMBER_OF
    }

    private final String field;
    private Object value;
    private Condition condition;
    private boolean not = false;

    public Filter(String field, Object value, Condition condition) {
        this.field = field;
        this.value = value;
        this.condition = condition;
        init();
    }

    public void setValue(Object value) {
        this.value = value;
        init();
    }

    @Override
    public String toString() {
        return "[" + field + (not ? " NOT " : " ") + condition + " " + value + "]";
    }

    private void init() {
        if (value instanceof String) {
            String val = ((String) value).trim();

            if ("!".equals(val)) {
                this.condition = Condition.EMPTY;
            } else if (val.startsWith("!")) {
                this.not = true;
                this.value = val.substring(1);
            }
        }
    }
}
