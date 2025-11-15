package com.ovvium.services.util.common;

import lombok.val;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Utils {

    public static final <T> Set<T> with(Set<T> set, T element) {
        val x = new HashSet<T>(set);
        x.add(element);
        return x;
    }

    public static final <T> List<T> with(List<T> list, T element) {
        val x = new ArrayList<T>(list);
        x.add(element);
        return x;
    }

}
