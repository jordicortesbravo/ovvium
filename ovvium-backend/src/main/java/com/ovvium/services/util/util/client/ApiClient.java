package com.ovvium.services.util.util.client;

public interface ApiClient<R extends ApiRequest<R, T, O>, T, O extends ApiRequestOptions<O>> {

    O options();

    R request(String operation);

    // TODO: Hauria de ser URI enlloc d'String?
    R createRequest(String url);
}
