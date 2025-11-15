package com.ovvium.services.util.util.client.http;

import org.springframework.web.util.UriTemplate;

import com.ovvium.services.util.util.client.AbstractApiClient;
import com.ovvium.services.util.util.client.AbstractApiRequest;

public class HttpApiRequest extends AbstractApiRequest<HttpApiRequest, HttpResult, HttpApiRequestOptions> {

    public HttpApiRequest(UriTemplate template, AbstractApiClient<HttpApiRequest, HttpResult, HttpApiRequestOptions> client) {
        super(template, client);
    }
}
