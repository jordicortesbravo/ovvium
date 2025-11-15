package com.ovvium.services.util.common.type;

import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Type representing a normalized email address. Is validated at creation time, but in order to simplify isn't very strict. Thus, some valid
 * email addresses won't be accepted (e.g. john@localhost) and probably some incorrect ones will succeed.
 * 
 * @author sinuhe
 */
@EqualsAndHashCode
public class Email implements Serializable, Comparable<Email> {

    private static final long serialVersionUID = 40694152235344164L;

    private static final Pattern EMAIL_PATTERN = Pattern
            .compile("^[_a-zA-Z0-9-\\+]+(\\.[_a-zA-Z0-9-\\+]+)*@[a-zA-Z0-9\\-]+(\\.[a-zA-Z0-9\\-]+){1,}$");

    private final String value;

    public Email(String emailAddress) {
        this.value = normalize(emailAddress);
        if (!isValid(value)) {
            throw new IllegalArgumentException("Email address format is not valid: " + value);
        }
    }

    @Override
    public String toString() {
        return value;
    }

    public String getLocalPart() {
        return value.substring(0, value.lastIndexOf('@'));
    }

    public String getDomain() {
        return value.substring(value.lastIndexOf('@') + 1);
    }

    public static String normalize(String text) {
        return text.trim().toLowerCase(Locale.US);
    }

    public static boolean isValid(String address) {
        return EMAIL_PATTERN.matcher(normalize(address)).matches();
    }

    @Override
    public int compareTo(Email o) {
        return value.compareTo(o.value);
    }

}
