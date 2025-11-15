package com.ovvium.services.util.util.el;

import org.springframework.core.convert.converter.Converter;

public class ExpConverter implements Converter<String, Exp> {

    private final ExpFactory factory = new ExpFactory();

    @Override
    public Exp convert(String source) {
        return factory.get(source);
    }

}
