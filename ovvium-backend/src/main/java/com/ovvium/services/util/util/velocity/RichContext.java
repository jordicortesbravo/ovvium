package com.ovvium.services.util.util.velocity;

import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.springframework.context.ApplicationContext;

public class RichContext extends VelocityContext {

    private final ApplicationContext applicationContext;

    public RichContext(Context context, Map<String, ? extends Object> model, ApplicationContext applicationContext) {
        super(model, context);
        this.applicationContext = applicationContext;
    }

    @Override
    public Object internalGet(String key) {
        if (applicationContext.containsBean(key)) {
            return applicationContext.getBean(key);
        }
        return super.internalGet(key);
    }

    @Override
    public boolean internalContainsKey(Object key) {
        if (key instanceof String && applicationContext.containsBean((String) key)) {
            return true;
        }
        return super.internalContainsKey(key);
    }

    @Override
    public Object[] internalGetKeys() {
        return ArrayUtils.addAll(super.internalGetKeys(), applicationContext.getBeanDefinitionNames());
    }

}
