package com.ovvium.services.util.util.client.xson;

import com.ovvium.services.util.util.client.ApiRequestOptions;
import com.ovvium.services.util.util.xson.Xson;
import com.ovvium.services.util.util.xson.XsonFactory;
import com.ovvium.services.util.util.xson.XsonFactoryConfigurer;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;



@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true, fluent = true)
public class XsonApiRequestOptions extends ApiRequestOptions<XsonApiRequestOptions> {

    private final XsonFactoryConfigurer configurer;
    private XsonFactory factory;

    public XsonApiRequestOptions() {
        configurer = Xson.configurer();
    }

    public XsonFactory getFactory() {
        if (factory == null) {
            factory = configurer.build();
        }
        return factory;
    }

    @Override
    public XsonApiRequestOptions clone() {
        // TODO: Pensar com clonar el configurer
        return copyTo(new XsonApiRequestOptions(configurer));
    }
}
