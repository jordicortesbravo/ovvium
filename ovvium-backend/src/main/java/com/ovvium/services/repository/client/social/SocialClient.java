package com.ovvium.services.repository.client.social;

import com.ovvium.services.util.ovvium.encoding.EncodingUtils;
import com.ovvium.services.util.util.xson.Xson;
import lombok.val;
import org.springframework.web.util.UriUtils;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

public abstract class SocialClient {
    
    protected <T> Optional<T> get(Xson json, String path, Function<Xson, T> fn) {
        val node = json.get(path);
        return node.isNull() ? Optional.empty() : Optional.of(fn.apply(node));
    }
    
    protected Xson decodeJWTBody(String jwtToken) {
        // JWT has header, body and signature encoded in Bae64, separated by .
        val jwtBody = jwtToken.split("\\.")[1];
        String body = new String(EncodingUtils.decodeB64(jwtBody,true));
        return Xson.create(body);
    }
    
    protected String toQueryString(Map<String, Object> args) {
        return toQueryString(args, false);
    }

    protected String toQueryString(Map<String, Object> args, boolean encode) {
        return args.entrySet().stream() //
                .map(e -> e.getKey() + "=" + (encode ? encodeURI(e.getValue().toString()) : e.getValue())) //
                .sorted() //
                .collect(Collectors.joining("&"));
    }

    protected String encodeURI(String uri) {
        return UriUtils.encode(uri, UTF_8);
    }

}
