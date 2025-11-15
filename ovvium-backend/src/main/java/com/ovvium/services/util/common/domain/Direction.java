package com.ovvium.services.util.common.domain;

import java.util.Locale;

public enum Direction {

    ASC, DESC;

    public static final Direction DEFAULT_DIRECTION = Direction.ASC;

    public static Direction fromString(String value) {
        return Direction.valueOf(value.toUpperCase(Locale.US));
    }
}
