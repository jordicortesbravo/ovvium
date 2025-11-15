package com.ovvium.services.util.util.client;

import java.net.URI;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;

public interface ApiRequest<R extends ApiRequest<R, T, O>, T, O extends ApiRequestOptions<O>> {

    URI getUri();

    R put(String name, Object value);

    R put(Map<String, ?> map);

    R header(String name, String value);

    R queryString(String queryString);

    T get();

    T post(String body, ContentType type);

    T post(HttpEntity entity);
    
    T patch(HttpEntity entity);

    default T delete(HttpEntity entity) {
        return delete();
    }

    T delete();

	T put(HttpEntity entity);

}
