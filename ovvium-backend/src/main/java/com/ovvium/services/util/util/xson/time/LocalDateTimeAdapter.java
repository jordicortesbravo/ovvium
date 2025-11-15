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
import java.time.LocalDateTime;

@RequiredArgsConstructor
public final class LocalDateTimeAdapter implements JsonDeserializer<LocalDateTime>, JsonSerializer<LocalDateTime> {

    public static final Type TYPE = TypeToken.get(LocalDateTime.class).getType();

    @Override
    public LocalDateTime deserialize(JsonElement el, Type type, JsonDeserializationContext ctx) {
        return el.isJsonNull() ? null : LocalDateTime.parse(el.getAsString());
    }

    @Override
    public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        return src == null ? null : new JsonPrimitive(src.toString());
    }
}
