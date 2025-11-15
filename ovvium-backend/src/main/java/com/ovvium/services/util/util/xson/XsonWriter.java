package com.ovvium.services.util.util.xson;

import java.io.StringWriter;
import java.nio.CharBuffer;

import lombok.SneakyThrows;

import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;

public class XsonWriter {

    private final Xson xson;
    private final StringWriter stringWriter;
    private final JsonWriter jsonWriter;

    public XsonWriter(Xson xson) {
        this.xson = xson;
        stringWriter = new StringWriter();
        jsonWriter = new JsonWriter(stringWriter);
    }

    public XsonWriter setLenient(boolean lenient) {
        jsonWriter.setLenient(lenient);
        return this;
    }

    public XsonWriter setHtmlSafe(boolean htmlSafe) {
        jsonWriter.setHtmlSafe(htmlSafe);
        return this;
    }

    public XsonWriter setSerializeNulls(boolean serializeNulls) {
        jsonWriter.setSerializeNulls(serializeNulls);
        return this;
    }

    public XsonWriter setIndent(String indent) {
        jsonWriter.setIndent(indent);
        return this;
    }

    public XsonWriter setIndent(int indent) {
        return setIndent(CharBuffer.allocate(indent).toString().replace('\0', ' '));
    }

    @Override
    @SneakyThrows
    public String toString() {
        if (xson.isNull()) {
            return "";
        }
        Streams.write(xson.unwrap(), jsonWriter);
        return stringWriter.toString();
    }

}
