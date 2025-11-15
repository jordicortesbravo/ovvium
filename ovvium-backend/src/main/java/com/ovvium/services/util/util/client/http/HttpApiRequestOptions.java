package com.ovvium.services.util.util.client.http;


import com.ovvium.services.util.util.client.ApiRequestOptions;

public class HttpApiRequestOptions extends ApiRequestOptions<HttpApiRequestOptions> {

    @Override
    public HttpApiRequestOptions clone() {
        return copyTo(new HttpApiRequestOptions());
    }

}
