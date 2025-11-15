package com.ovvium.services.model.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.val;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.ovvium.services.model.common.LocaleConstants.DEFAULT_LOCALE;
import static com.ovvium.services.util.ovvium.base.Preconditions.checkNotBlank;
import static lombok.AccessLevel.PROTECTED;

@Embeddable
@Accessors(chain = true)
@NoArgsConstructor(access = PROTECTED)
public class MultiLangString {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setSerializationInclusion(NON_NULL);

    @Getter
    @NotNull
    private String defaultValue;

    private String translations;

    public MultiLangString(String defaultValue) {
        this.defaultValue = checkNotBlank(defaultValue, "Default value can't be blank");
        setTranslations(Collections.singletonMap(DEFAULT_LOCALE, defaultValue));
    }

    @SneakyThrows
    public Map<Locale, String> getTranslations() {
        var tr = new HashMap<Locale, String>();
        var json = OBJECT_MAPPER.readTree(this.translations);
        json.fieldNames().forEachRemaining(n -> tr.put(Locale.forLanguageTag(n), json.get(n).asText()));
        tr.putIfAbsent(DEFAULT_LOCALE, defaultValue);
        return Collections.unmodifiableMap(tr);
    }

    @SneakyThrows
    private MultiLangString setTranslations(Map<Locale, String> translations) {
        val map = translations.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().toLanguageTag(), Map.Entry::getValue));
        this.translations = OBJECT_MAPPER.writeValueAsString(map);
        return this;
    }

    public static MultiLangString ofDefaultAndTranslations(String defaultValue, Map<String, String> translationMap) {
        val multiLangString = new MultiLangString(defaultValue);
        val translations = translationMap.entrySet().stream()
                .collect(Collectors.toMap(e -> Locale.forLanguageTag(e.getKey()), Map.Entry::getValue));
        translations.putIfAbsent(DEFAULT_LOCALE, defaultValue);
        return multiLangString.setTranslations(translations);
    }

}

