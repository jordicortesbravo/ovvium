package com.ovvium.services.util.util.client;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Executor;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriTemplate;

import com.ovvium.services.util.util.xprops.XProps;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;
import lombok.experimental.Accessors;

// TODO: Poder passar un logger per paràmetre, i que el nom per defecte no sigui el d'una inner class
public abstract class AbstractApiClient<R extends ApiRequest<R, T, O>, T, O extends ApiRequestOptions<O>>
		implements ApiClient<R, T, O> {

	private final String baseUri;
	private final Map<String, UriTemplate> uris;

	@Getter
	@Accessors(fluent = true)
	private final O options;

	@Getter
	protected Executor executor = Executor.newInstance();

	public AbstractApiClient(O options) {
		this(options, "", Collections.<Entry<String, String>>emptyList());
	}

	public AbstractApiClient(O options, XProps p) {
		this(options, p.get("url"), p.sub("path"));
	}

	public AbstractApiClient(O options, String baseUri, Iterable<Entry<String, String>> operationPaths) {
		this.options = options;
		this.baseUri = baseUri == null ? "" : baseUri;
		executor.use(new BasicCookieStore());
		val uris = new HashMap<String, UriTemplate>();
		for (Entry<String, String> e : operationPaths) {
			uris.put(e.getKey(), new UriTemplate(this.baseUri + e.getValue()));
		}
		this.uris = Collections.unmodifiableMap(uris);

		if (options.log() == null) {
			options.log(LoggerFactory.getLogger(this.getClass()));
		}
	}

    public void allowUnsecureSsl() {
        val builder = HttpClientBuilder.create();
        ClientUtils.setUnsecureSsl(builder);
        executor = Executor.newInstance(builder.build()); //
    }

	@Override
	public R request(String operation) {
		return createRequest(uris.get(operation));
	}

	@Override
	public R createRequest(String path) {
		return createRequest(new UriTemplate(baseUri + path));
	}

	/**
	 * @return content as String using charset defined in options. It will be
	 *         null if there's no content
	 */
	@SneakyThrows
	protected String getContentAsString(HttpResponse response) {
		val entity = response.getEntity();
		return entity == null ? null : EntityUtils.toString(entity, options.charset());
	}

	protected abstract R createRequest(UriTemplate uriTemplate);

	protected abstract T createResponse(HttpResponse response);

	/**
	 * return empty object
	 */
	protected abstract T empty();
}
