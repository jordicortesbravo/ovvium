package com.ovvium.services.util.util.xson.adapter;

import java.lang.reflect.Type;

import com.google.gson.*;
import com.ovvium.services.util.common.domain.Identifiable;
import com.ovvium.services.util.common.domain.Identifiables;

import lombok.val;

public class IdentifiableEnumAdapter<T extends Enum<T> & Identifiable<S>, S> extends PathAdapter<T> {

    private final Gson config = new Gson();

    @Override
    public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        @SuppressWarnings("unchecked") Class<T> enumClss = (Class<T>) typeOfT;
        val idClass = Identifiables.getIdClass(enumClss);
        return Identifiables.get(enumClss, config.fromJson(super.getFromPath(json), idClass));
    }

    @Override
    public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
        val value = context.serialize(src.getId());
        return setInPath(value);
    }

}
