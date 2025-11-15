package com.ovvium.services.util.util.xson;

import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapterFactory;
import com.ovvium.services.util.util.xson.time.InstantAdapter;
import com.ovvium.services.util.util.xson.time.LocalDateAdapter;
import com.ovvium.services.util.util.xson.time.LocalDateTimeAdapter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.lang.reflect.Type;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class XsonFactoryConfigurer {

    @Getter
    private final GsonBuilder delegate = new GsonBuilder();

    public XsonFactoryConfigurer setPrettyPrinting() {
        this.delegate.setPrettyPrinting();
        return this;
    }

    public XsonFactory build() {
        delegate.registerTypeAdapter(LocalDateAdapter.TYPE, new LocalDateAdapter());
        delegate.registerTypeAdapter(LocalDateTimeAdapter.TYPE, new LocalDateTimeAdapter());
        delegate.registerTypeAdapter(InstantAdapter.TYPE, new InstantAdapter());
        return new XsonFactory(delegate.create());
    }

    public XsonFactoryConfigurer registerTypeAdapter(Type type, Object typeAdapter) {
        delegate.registerTypeAdapter(type, typeAdapter);
        return this;
    }

    public <T> XsonFactoryConfigurer registerTypeAdapterFactory(TypeAdapterFactory typeAdapterFactory) {
        delegate.registerTypeAdapterFactory(typeAdapterFactory);
        return this;
    }

}
