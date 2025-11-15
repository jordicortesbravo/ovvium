package com.ovvium.services.model.common;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static com.ovvium.services.model.common.LocaleConstants.CATALAN;
import static com.ovvium.services.model.common.LocaleConstants.SPANISH;
import static java.util.Locale.UK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MultiLangStringTest {

    private static final String SPANISH_SENTENCE = "frase en castellano";
    private static final String CATALAN_SENTENCE = "frase en català";
    private static final String ENGLISH_SENTENCE = "sentence in english";

    @Test
    public void given_null_defaultValue_when_create_multiLangString_then_throw_exception() {
        assertThatThrownBy(() -> new MultiLangString(null)) //
                .isExactlyInstanceOf(IllegalArgumentException.class)//
                .hasMessage("Default value can't be blank");
    }

    @Test
    public void given_defaultValue_and_translations_when_create_multiLangString_then_create_everything_properly() {

        var translations = new HashMap<String, String>();
        translations.put("ca-ES", CATALAN_SENTENCE);
        translations.put("en-GB", ENGLISH_SENTENCE);
        var multiLangString = MultiLangString.ofDefaultAndTranslations(SPANISH_SENTENCE, translations);

        assertThat(multiLangString.getDefaultValue()).isEqualTo(SPANISH_SENTENCE);
        assertThat(multiLangString.getTranslations().get(SPANISH)).isEqualTo(SPANISH_SENTENCE);
        assertThat(multiLangString.getTranslations().get(CATALAN)).isEqualTo(CATALAN_SENTENCE);
        assertThat(multiLangString.getTranslations().get(UK)).isEqualTo(ENGLISH_SENTENCE);
    }
}
