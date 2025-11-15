package com.ovvium.services.util.util.client;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import lombok.Data;
import lombok.experimental.Accessors;

import org.slf4j.Logger;

@Data
@Accessors(chain = true, fluent = true)
public abstract class ApiRequestOptions<O extends ApiRequestOptions<O>> {

    private int timeoutMillis = 5000;
    private Charset charset = StandardCharsets.UTF_8;
    private OnNotFound on404 = OnNotFound.EXCEPTION;
    private Logger log;
    private boolean encodeUri = true;

    public O timeoutMillis(int timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
        return me();
    };

    public O charset(Charset charset) {
        this.charset = charset;
        return me();
    };

    public O on404(OnNotFound on404) {
        this.on404 = on404;
        return me();
    };

    public O log(Logger log) {
        this.log = log;
        return me();
    };
    
    public O encodeUri(boolean encodeUri) {
        this.encodeUri = encodeUri;
        return me();
    };

    @Override
    public abstract O clone();

    protected O copyTo(O other) {
        return other.charset(charset) //
                .on404(on404) //
                .timeoutMillis(timeoutMillis) //
                .log(log)//
                .encodeUri(encodeUri);
    }

    @SuppressWarnings("unchecked")
    protected O me() {
        return (O) this;
    }
}
