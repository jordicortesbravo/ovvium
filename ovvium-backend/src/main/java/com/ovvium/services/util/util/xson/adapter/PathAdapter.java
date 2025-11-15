package com.ovvium.services.util.util.xson.adapter;

import com.google.gson.*;
import com.ovvium.services.util.util.basic.Path;
import com.ovvium.services.util.util.xson.Xson;

import lombok.val;

public abstract class PathAdapter<T> implements JsonDeserializer<T>, JsonSerializer<T> {

    private String path;

    public PathAdapter<T> setPath(String path) {
        this.path = path;
        return this;
    }

    protected JsonElement getFromPath(JsonElement el) {
        return path == null ? el : Xson.get(el, path);
    }

    // TODO: Només accepta paths de l'estil a/b/c, sense [0], per exemple
    // TODO: Passar a Xson? JsonUtils?
    protected JsonElement setInPath(JsonElement current) {
        if (path != null) {
            for (val p : Path.of(this.path).reverse()) {
                val newObj = new JsonObject();
                newObj.add(p.getName(), current);
                current = newObj;
            }
        }
        return current;
    }
}
