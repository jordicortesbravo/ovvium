package com.ovvium.services.util.util.client.http;

import java.nio.charset.Charset;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;

@Getter
@RequiredArgsConstructor
public class HttpResult {

    private final HttpResponse response;
    private final Charset charset;
    private byte[] content;

    @SneakyThrows
    public byte[] getContent() {
        if (content == null && response != null) {
            val entity = response.getEntity();
            if (entity != null) {
                content = IOUtils.toByteArray(entity.getContent());
            }
        }
        return content;
    }

    public int getStatus() {
        return response == null ? 0 : response.getStatusLine().getStatusCode();
    }

    @Override
    public String toString() {
        val content = getContent();
        return content == null ? "" : new String(content, charset);
    }
}
