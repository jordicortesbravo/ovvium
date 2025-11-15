package com.ovvium.services.util.util.client;

import lombok.Getter;
import org.apache.http.StatusLine;

import java.net.URI;

@Getter
@SuppressWarnings("serial")
public class ClientException extends RuntimeException {

    private URI url;
    private int status;
    private String reason;
    private String body;

    public ClientException(URI url, StatusLine status, String body) {
        super(message(url, status, body));
        init(url, status);
    }

    public ClientException(URI url, StatusLine status, Throwable cause) {
        super(message(url, status, null), cause);
        init(url, status);
    }

    private static String message(URI url, StatusLine status, String body) {
        return url.toString() + (status == null ? " (Incomplete)" : " (" + status.getStatusCode() + " " + status.getReasonPhrase() + ")") + " ResponseBody: " + body;
    }

    private void init(URI url, StatusLine status) {
        this.url = url;
        this.status = status == null ? 0 : status.getStatusCode();
        this.reason = status == null ? "" : status.getReasonPhrase();
    }

    public ClientException setBody(String body) {
        this.body = body;
        return this;
    }
}
