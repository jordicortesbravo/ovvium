package com.ovvium.services.util.util.io;

import java.io.InputStream;
import java.nio.charset.Charset;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IOUtils {

    public static InputStream getResource(String path) {
        val cl = IOUtils.class.getClassLoader();
        return cl.getResourceAsStream(path);
    }

    @SneakyThrows
    public static byte[] getResourceAsBytes(String path) {
        return org.apache.commons.io.IOUtils.toByteArray(getResource(path));
    }

    public static String getResourceAsString(String path, Charset charset) {
        return new String(getResourceAsBytes(path), charset);
    }

}
