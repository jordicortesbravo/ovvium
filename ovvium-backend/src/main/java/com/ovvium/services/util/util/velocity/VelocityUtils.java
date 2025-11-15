package com.ovvium.services.util.util.velocity;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.velocity.VelocityContext;

import java.util.Locale;
import java.util.Map;

/**
 * 
 *         Utilidad para evaluar expresiones Velocity
 * 
 */

public final class VelocityUtils {

    private VelocityUtils() {}

    @Setter
    @Getter
    private static VelocityManager velocityManager;

    public static String eval(String inlineTemplate, Map<String, Object> parameters) {

        init();

        VelocityContext velocityContext = velocityManager.getContext(parameters);

        return velocityManager.eval(inlineTemplate, velocityContext);
    }

    /**
     * Evalua la plantilla de Velocity pasada como @param templatePath
     */
    public static String run(String templatePath, Map<String, Object> parameters) {

        init();

        VelocityContext velocityContext = velocityManager.getContext(parameters);

        return velocityManager.run(templatePath, velocityContext);
    }

    /**
     * Evalua la plantilla de Velocity pasada como @param templatePath
     */
    public static String run(String templatePath, Map<String, Object> parameters, Locale locale) {

        init();

        VelocityContext velocityContext = velocityManager.getContext(parameters);

        return velocityManager.run(templatePath, locale, velocityContext);
    }

    /**
     * Inicialización de la condifguración de Velocity
     */
    @SneakyThrows
    private static synchronized void init() {

        if (velocityManager != null) {
            return;
        }

        velocityManager = new VelocityManager();
        velocityManager.initialize();
    }
}
