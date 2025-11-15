package com.ovvium.services.util.jpa.core;

import org.springframework.orm.jpa.vendor.Database;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataSourceInfo {

    private Database type;
    private String url;
    private String username;
    private String password;
    private SchemaGeneration generation;
    private Integer idleTestSeconds;
    private String jndiName;
}
