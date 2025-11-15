package com.ovvium.services.util.util.client.xson;

import org.springframework.web.util.UriTemplate;

import com.ovvium.services.util.util.client.AbstractApiClient;
import com.ovvium.services.util.util.client.AbstractApiRequest;
import com.ovvium.services.util.util.xprops.XProps;
import com.ovvium.services.util.util.xson.Xson;



public class XsonApiClient extends AbstractXsonApiClient<XsonApiClient.XsonApiRequest> {

    public static class XsonApiRequest extends AbstractApiRequest<XsonApiRequest, Xson, XsonApiRequestOptions> {

        public XsonApiRequest(UriTemplate template, AbstractApiClient<XsonApiRequest, Xson, XsonApiRequestOptions> client) {
            super(template, client);
        }
    }

    public XsonApiClient() {
        super();
    }

    public XsonApiClient(XProps p) {
        super(p);
    }

    @Override
    protected XsonApiRequest createRequest(UriTemplate uriTemplate) {
        return new XsonApiRequest(uriTemplate, this);
    }

    @Override
    protected Xson empty() {
        return this.options().getFactory().createNull();
    }

}
