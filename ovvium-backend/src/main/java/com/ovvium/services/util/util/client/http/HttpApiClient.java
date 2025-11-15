package com.ovvium.services.util.util.client.http;

import org.apache.http.HttpResponse;
import org.springframework.web.util.UriTemplate;

import com.ovvium.services.util.util.client.AbstractApiClient;
import com.ovvium.services.util.util.xprops.XProps;

public class HttpApiClient extends AbstractApiClient<HttpApiRequest, HttpResult, HttpApiRequestOptions> {

    public HttpApiClient() {
        super(new HttpApiRequestOptions());
    }

    public HttpApiClient(XProps p) {
        super(new HttpApiRequestOptions(), p.get("url"), p.sub("path"));
    }

    @Override
    protected HttpApiRequest createRequest(UriTemplate template) {
        return new HttpApiRequest(template, this);
    }

    @Override
    protected HttpResult createResponse(HttpResponse response) {
        return new HttpResult(response, options().charset());
    }

    @Override
    protected HttpResult empty() {
        return new HttpResult(null, options().charset());
    }

}
