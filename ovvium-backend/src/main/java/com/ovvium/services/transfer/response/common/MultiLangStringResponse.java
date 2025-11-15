package com.ovvium.services.transfer.response.common;

import com.ovvium.services.model.common.MultiLangString;
import lombok.Getter;

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@Getter
public final class MultiLangStringResponse implements Serializable {

    private final String defaultValue;
    private final Map<String, String> translations;

    public MultiLangStringResponse(MultiLangString multiLangString) {
        this.defaultValue = multiLangString.getDefaultValue();
        this.translations = multiLangString.getTranslations()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(t -> t.getKey().toLanguageTag(), Entry::getValue));
    }
}
