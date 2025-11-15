package com.ovvium.services.util.util.reflection;

import java.lang.reflect.Member;

public class MultipleFilter<T extends Member> implements MemberFilter<T> {

    private final MemberFilter<T>[] filters;

    @SafeVarargs
    public MultipleFilter(MemberFilter<T>... filters) {
        this.filters = filters;
    }

    @Override
    public boolean match(T member) {
        for (MemberFilter<T> filter : filters) {
            if (!filter.match(member)) {
                return false;
            }
        }
        return true;
    }

}
