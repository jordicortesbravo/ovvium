package com.ovvium.services.util.util.xprops;

import java.util.List;

import lombok.Getter;
import lombok.val;

import org.springframework.expression.spel.SpelEvaluationException;

@SuppressWarnings("serial")
public class XPropsResolverException extends RuntimeException {

    @Getter
    private final List<SpelEvaluationException> list;

    public XPropsResolverException(List<SpelEvaluationException> list) {
        super("Exceptions occurred during variable resolution: " + getString(list));
        this.list = list;
    }

    public int size() {
        return list.size();
    }

    public static String getString(List<SpelEvaluationException> list) {
        val sb = new StringBuilder("[ ");
        for (val e : list) {
            sb.append(e.getMessage()).append(", ");
        }
        sb.setCharAt(sb.length() - 1, ']');
        return sb.toString();
    }
}
