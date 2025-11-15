package com.ovvium.services.util.util.xson.time;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Type;
import java.time.LocalDate;

@RequiredArgsConstructor
public final class LocalDateAdapter implements JsonDeserializer<LocalDate>, JsonSerializer<LocalDate> {

    public static final Type TYPE = TypeToken.get(LocalDate.class).getType();

    @Override
    public LocalDate deserialize(JsonElement el, Type type, JsonDeserializationContext ctx) {
        return el.isJsonNull() ? null : LocalDate.parse(el.getAsString());
    }

    @Override
    public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
        return src == null ? null : new JsonPrimitive(src.toString());
    }
}
