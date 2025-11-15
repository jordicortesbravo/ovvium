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
import java.time.Instant;

@RequiredArgsConstructor
public final class InstantAdapter implements JsonDeserializer<Instant>, JsonSerializer<Instant> {

    public static final Type TYPE = TypeToken.get(Instant.class).getType();

    @Override
    public Instant deserialize(JsonElement el, Type type, JsonDeserializationContext ctx) {
        return el.isJsonNull() ? null : Instant.parse(el.getAsString());
    }

    @Override
    public JsonElement serialize(Instant src, Type typeOfSrc, JsonSerializationContext context) {
        return src == null ? null : new JsonPrimitive(src.toString());
    }
}
