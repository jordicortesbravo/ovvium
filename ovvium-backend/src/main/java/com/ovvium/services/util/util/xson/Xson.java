package com.ovvium.services.util.util.xson;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.ovvium.services.util.common.domain.Page;
import com.ovvium.services.util.common.domain.SimplePage;
import com.ovvium.services.util.util.io.IOUtils;
import com.ovvium.services.util.util.reflection.ReflectionUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static com.ovvium.services.util.util.xson.XsonFactory.INSTANCE;
import static java.nio.charset.StandardCharsets.UTF_8;

@RequiredArgsConstructor
@EqualsAndHashCode(of = "json")
public class Xson implements Iterable<Xson> {

    public enum Type {
        OBJECT, ARRAY, PRIMITIVE, NULL
    }

    @Getter
    private final Gson config;
    private final JsonElement json;

    public Type getType() {
        return json instanceof JsonObject ? Type.OBJECT //
                : json instanceof JsonArray ? Type.ARRAY //
                        : json instanceof JsonPrimitive ? Type.PRIMITIVE //
                                : Type.NULL;
    }

    // TODO: buff... json path?
    public Xson get(String path) {
        return new Xson(config, get(json, path));
    }

    public Xson get(int index) {
        return new Xson(config, get(json, index));
    }

    public boolean isNull() {
        return json == null || json.isJsonNull();
    }

    public List<Xson> asList() {
        val list = new ArrayList<Xson>();
        if (!isNull()) {
            for (val el : json.getAsJsonArray()) {
                list.add(new Xson(config, el));
            }
        }
        return list;
    }

    public <T> List<T> asList(Class<T> clss) {
        val list = new ArrayList<T>();
        for (val el : asList()) {
            list.add(el.as(clss));
        }
        return list;
    }

    @Deprecated
    @SuppressWarnings("unchecked")
    public <T> Page<T> asPage(Class<T> clss) {
        Page<T> page = as(SimplePage.class);
        val contentXson = Xson.of(page.getContent());
        return page.update(contentXson.asList(clss));
    }

    public Map<String, Xson> asMap() {
        val map = new LinkedHashMap<String, Xson>();
        if (!isNull()) {
            for (Entry<String, JsonElement> el : json.getAsJsonObject().entrySet()) {
                map.put(el.getKey(), new Xson(config, el.getValue()));
            }
        }
        return map;
    }

    public <K, V> Map<K, V> asMap(Class<K> keyClass, Class<V> valueClass) {
        val map = new LinkedHashMap<K, V>();
        for (Entry<String, Xson> kv : asMap().entrySet()) {
            val keyString = '"' + kv.getKey().replace("\"", "\\\"") + '"';
            val key = config.fromJson(keyString, keyClass);
            map.put(key, kv.getValue().as(valueClass));
        }
        return map;
    }

    public Object asIs() {
        return isNull() ? null : ReflectionUtils.get(json.getAsJsonPrimitive(), "value");
    }

    public String asString() {
        return json.getAsString();
    }

    public URI asUrl() {
        return URI.create(asString());
    }

    public Integer asInt() {
        return isNull() ? null : json.getAsInt();
    }

    public <T> T as(Class<T> clss) {
        return config.fromJson(json, clss);
    }

    public <T> T as(java.lang.reflect.Type type) {
        return config.fromJson(json, type);
    }

    @Override
    public String toString() {
        return writer().setIndent(2).toString();
    }

    public XsonWriter writer() {
        return new XsonWriter(this);
    }

    @Override
    public Iterator<Xson> iterator() {
        return asList().iterator();
    }

    public JsonElement unwrap() {
        return json;
    }

    public Xson clone() {
        val copy = new JsonParser().parse(json.toString());
        return new Xson(config, copy);
    }

    // Mutators

    public Xson set(String name, Object value) {
        val element = value instanceof Xson ? ((Xson) value).unwrap() : config.toJsonTree(value);
        ((JsonObject) json).add(name, element);
        return this;
    }

    public Xson remove(String name) {
        if (json instanceof JsonObject) {
            ((JsonObject) json).remove(name);
        }
        return this;
    }

    // Static methods

    public static XsonFactoryConfigurer configurer() {
        return new XsonFactoryConfigurer();
    }

    public static Xson ofResource(String fileUri) {
        return INSTANCE.create(IOUtils.getResourceAsString(fileUri, UTF_8));
    }

    public static Xson create(String json) {
        return INSTANCE.create(json);
    }

    public static Xson of(Object o) {
        return INSTANCE.of(o);
    }

    public static JsonElement get(JsonElement current, String path) {
        for (String p : path.split("/")) {
            if (current == null || current.isJsonNull()) {
                return JsonNull.INSTANCE;
            }
            int index = -1;
            if (p.endsWith("]")) {
                int start = p.indexOf('[');
                index = Integer.parseInt(p.substring(start + 1, p.length() - 1));
                p = p.substring(0, start);
            }
            current = current.getAsJsonObject().get(p);
            if (index > -1) {
                current = get(current, index);
            }
        }
        return current;
    }

    private static JsonElement get(JsonElement el, int index) {
        if (el != null && el.isJsonArray()) {
            val array = el.getAsJsonArray();
            if (array.size() > index) {
                return array.get(index);
            }
        }
        return JsonNull.INSTANCE;
    }

}
