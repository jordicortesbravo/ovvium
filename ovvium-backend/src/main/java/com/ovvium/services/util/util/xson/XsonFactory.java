package com.ovvium.services.util.util.xson;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class XsonFactory {

    public static final XsonFactory INSTANCE = Xson.configurer().build();

    @Getter
    private final Gson config;

    public Xson wrap(JsonElement json) {
        return new Xson(config, json);
    }

    public Xson create(String json) {
        return wrap(new JsonParser().parse(json));
    }

    public Xson createNull() {
        return wrap(JsonNull.INSTANCE);
    }

    public Xson of(Object o) {
        return wrap(config.toJsonTree(o));
    }

}
