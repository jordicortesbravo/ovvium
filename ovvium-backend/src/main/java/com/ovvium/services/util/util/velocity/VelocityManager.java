package com.ovvium.services.util.util.velocity;

import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.springframework.context.ApplicationContext;

@Slf4j
public class VelocityManager {

    @Resource
    private ApplicationContext applicationContext;

    @Getter
    private VelocityEngine velocityEngine = new VelocityEngine();

    @Setter
    private String basePath = "";

    @PostConstruct
    public void initialize() {

        log.info("Initializing Velocity engine...");

        velocityEngine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM, new Slf4jLogChute());

        velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, PrefixResourceLoader.NAME);
        velocityEngine.setProperty(PrefixResourceLoader.RESOURCE_LOADER_CLASS, PrefixResourceLoader.class.getName());
        velocityEngine.setProperty(PrefixResourceLoader.RESOURCE_LOADER_CACHE, "false");
        velocityEngine.setProperty(PrefixResourceLoader.RESOURCE_BASE_PATH, basePath);

        velocityEngine.init();
    }

    public void setProperty(String key, Object value) {
        velocityEngine.setProperty(key, value);
    }

    public void setMacroAutoReload(boolean reload) {
        setProperty("velocimacro.library.autoreload", reload + "");
        setProperty("file.resource.loader.cache", !reload + "");
        setProperty(RuntimeConstants.VM_PERM_ALLOW_INLINE_REPLACE_GLOBAL, reload);
    }

    public VelocityContext getContext(Map<String, ? extends Object> model) {
        if (applicationContext == null) {
            return new VelocityContext(model);
        }
        return new RichContext(null, model, applicationContext);
    }

    public VelocityContext getContext() {
        return getContext(null);
    }

    public Template getTemplate(String name) {
        return getFirstTemplate(new String[] { name });
    }

    public Template getTemplate(String name, Locale locale) {
        String[] candidates = new String[] { name + "." + locale, name + "." + locale.getLanguage(), name };
        return getFirstTemplate(candidates);
    }

    private Template getFirstTemplate(String[] candidates) {
        String name = null;
        for (String candidate : candidates) {
            name = candidate + ".vm";
            if (velocityEngine.resourceExists(name)) {
                return velocityEngine.getTemplate(name);
            }
        }
        throw new ResourceNotFoundException("Unable to find resource [" + name + "] in any resource loader.");
    }

    public String run(String templateName, Context context) {
        return run(getTemplate(templateName), context);
    }

    public String run(String templateName, Locale locale, Context context) {
        return run(getTemplate(templateName, locale), context);
    }

    public String run(Template template, Context context) {
        StringWriter sw = new StringWriter();
        template.merge(context, sw);
        return sw.toString();
    }

    public String eval(String inlineTemplate, Context context) {
        StringWriter sw = new StringWriter();
        velocityEngine.evaluate(context, sw, "template", inlineTemplate);
        return sw.toString();
    }
}
