package com.ovvium.services.util.util.client;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicHeader;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriTemplate;

import com.ovvium.services.util.util.string.StringUtils;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.val;

@Data
public abstract class AbstractApiRequest<R extends ApiRequest<R, T, O>, T, O extends ApiRequestOptions<O>> implements ApiRequest<R, T, O> {

    protected final Map<String, Object> map = new LinkedHashMap<String, Object>();
    protected final AbstractApiClient<R, T, O> client;
    protected final UriTemplate template;
    protected final List<Header> headers = new ArrayList<Header>();
    protected final O options;
    private String queryString;

    public AbstractApiRequest(UriTemplate template, AbstractApiClient<R, T, O> client) {
        this.template = template;
        this.client = client;
        this.options = client.options().clone();
    }

    public R timeoutMillis(int timeoutMillis) {
        options.timeoutMillis(timeoutMillis);
        return me();
    };

    public R charset(Charset charset) {
        options.charset(charset);
        return me();
    };

    public R on404(OnNotFound empty) {
        options.on404(empty);
        return me();
    };

    @Override
    @SneakyThrows
    public URI getUri() {
        val uri = options.encodeUri() ? template.expand(map)
                : URI.create(UriComponentsBuilder.fromUriString(template.toString()).build().expand(map).toUriString());
        val unusedParams = new HashMap<String, Object>(map);
        unusedParams.keySet().removeAll(template.getVariableNames());

        val query = new StringBuilder();
        for (Entry<String, Object> e : unusedParams.entrySet()) {
            addParam(query, e.getKey(), e.getValue());
        }
        if (queryString != null) {
            query.append('&').append(queryString);
        }
        if (uri.getQuery() == null && query.length() > 0) {
            query.replace(0, 1, "?");
        }

        // TODO: Hauríem de codificar abans de parsejar? fa por...
        return new URI(uri.toString() + query);
    }

    @Override
    public R put(String name, Object value) {
        if (value != null) {
            map.put(name, value);
        }
        return me();
    }

    @Override
    public R put(Map<String, ?> map) {
        for (Entry<String, ?> e : map.entrySet()) {
            put(e.getKey(), e.getValue());
        }
        return me();
    }

    @Override
    public T put(HttpEntity entity) {
        val uri = getUri();
        options.log().debug("PUT {} entity={}", uri, entity);
        return execute(uri, Request.Put(uri) //
                .body(entity));
    }

    @Override
    public T get() {
        val uri = getUri();
        options.log().debug("GET {}", uri);
        return execute(uri, Request.Get(uri));
    }

    @Override
    public T post(String body, ContentType type) {
        val uri = getUri();
        options.log().debug("POST {} body={}", uri, StringUtils.removeLineBreaks(body));
        type = (type == null ? ContentType.TEXT_PLAIN : type);
        return execute(uri, Request.Post(uri) //
                .bodyString(body, type));
    }

    @Override
    public T delete() {
        val uri = getUri();
        options.log().debug("DELETE {}", uri);
        return execute(uri, Request.Delete(uri));
    }

    @Override
    public T delete(HttpEntity entity) {
        val uri = getUri();
        options.log().debug("DELETE {} entity={}", uri, entity);
        return execute(uri, Request.Delete(uri) //
                .body(entity));
    }

    @Override
    public T post(HttpEntity entity) {
        val uri = getUri();
        options.log().debug("POST {} entity={}", uri, entity);
        return execute(uri, Request.Post(uri) //
                .body(entity));
    }

    @Override
    public T patch(HttpEntity entity) {
        val uri = getUri();
        options.log().debug("PATCH {} entity={}", uri, entity);
        return execute(uri, Request.Patch(uri) //
                .body(entity));
    }

    @Override
    public R header(String name, String value) {
        headers.add(new BasicHeader(name, value));
        return me();
    }

    @Override
    public R queryString(String queryString) {
        this.queryString = queryString;
        return me();
    }

    protected T execute(URI url, Request req) {
        for (val h : headers) {
            req.addHeader(h);
        }
        StatusLine status = null;
        try {
            req.connectTimeout(options.timeoutMillis()) //
                    .socketTimeout(options.timeoutMillis());
            val resp = client.getExecutor() //
                    .execute(req) //
                    .returnResponse();
            status = resp.getStatusLine();
            int code = status.getStatusCode();

            if (code == 404) {
                switch (options.on404()) {
                    case EMPTY:
                        return client.empty();
                    case NULL:
                        return null;
                    case EXCEPTION:
                        throw new ClientException(url, status, client.getContentAsString(resp));
                    case OK:
                        // Do nothing and just construct normal response
                }
            } else if (code < 200 || code >= 300) {
                throw new ClientException(url, status, client.getContentAsString(resp));
            }
            return client.createResponse(resp);
        } catch (ClientException e) {
            throw e;
        } catch (Exception e) {
            throw new ClientException(url, status, e);
        }
    }

    protected void addParam(StringBuilder query, String name, Object value) {
        if (value == null) {
            return;
        }
        if (value.getClass().isArray()) {
            addParam(query, name, Arrays.asList((Object[]) value));
        } else if (value instanceof Iterable) {
            for (val v : (Iterable<?>) value) {
                addParam(query, name, v);
            }
        } else {
            query.append('&').append(name).append('=').append(value);
        }
    }

    @SuppressWarnings("unchecked")
    protected R me() {
        return (R) this;
    }
}
