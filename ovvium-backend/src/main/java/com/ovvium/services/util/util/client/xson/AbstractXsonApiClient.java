package com.ovvium.services.util.util.client.xson;

import lombok.val;

import org.apache.http.HttpResponse;

import com.ovvium.services.util.util.client.AbstractApiClient;
import com.ovvium.services.util.util.client.ApiRequest;
import com.ovvium.services.util.util.xprops.XProps;
import com.ovvium.services.util.util.xson.Xson;


public abstract class AbstractXsonApiClient<R extends ApiRequest<R, Xson, XsonApiRequestOptions>> extends
        AbstractApiClient<R, Xson, XsonApiRequestOptions> {

    public AbstractXsonApiClient() {
        super(new XsonApiRequestOptions());
    }

    public AbstractXsonApiClient(XProps p) {
        super(new XsonApiRequestOptions(), p.get("url"), p.sub("path"));
    }

    @Override
    protected Xson createResponse(HttpResponse response) {
        val content = super.getContentAsString(response);
        return content == null ? null : options().getFactory().create(content);
    }
}
