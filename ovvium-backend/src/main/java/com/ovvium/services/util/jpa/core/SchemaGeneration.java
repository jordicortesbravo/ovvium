package com.ovvium.services.util.jpa.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SchemaGeneration {

    CREATE("create-only"), //
    DROP("drop"), //
    DROP_AND_CREATE("create"), //
    EXTEND("update"), //
    NONE("none");

    public static final String PROPERTY = "javax.persistence.schema-generation.database.action";

    // Hibernate value
    private final String value;

}
