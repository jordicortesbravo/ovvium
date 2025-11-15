package com.ovvium.services.util.util.velocity;

import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogChute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <b>Velocity</b> has its own logging system. This implementation redirects all messages to <b>Slf4j API</b>.
 * 
 * <ul>
 * 
 * <li>For use this logger, <i>EclipseLink</i>'s property <i>eclipselink.logging.logger</i> must be set to
 * <i>es.kcsolutions.core.jpa.eclipselink.EclipseLinkLogger</i>.</li>
 * 
 * <li>Property for using this logger can be set programatically:<br>
 * <code>VelocityUtils.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM, new Slf4jLogChute());</code></li>
 * 
 * <li>Logger's name will be <i>org.apache.velocity</i>, with no option to change it.</li>
 * 
 * </ul>
 * 
 */
public class Slf4jLogChute implements LogChute {

    public static final String DEFAULT_LOG_NAME = "org.apache.velocity";

    private final Logger logger = LoggerFactory.getLogger(DEFAULT_LOG_NAME);

    @Override
    public void init(RuntimeServices rs) {}

    @Override
    public void log(int level, String message) {
        switch (level) {
            case TRACE_ID:
                logger.trace(message);
                break;
            case DEBUG_ID:
                logger.debug(message);
                break;
            case INFO_ID:
                logger.info(message);
                break;
            case WARN_ID:
                logger.warn(message);
                break;
            case ERROR_ID:
                logger.error(message);
                break;
            default:
                logger.error(message);
                break;
        }
    }

    @Override
    public void log(int level, String message, Throwable t) {
        switch (level) {
            case TRACE_ID:
                logger.trace(message, t);
                break;
            case DEBUG_ID:
                logger.debug(message, t);
                break;
            case INFO_ID:
                logger.info(message, t);
                break;
            case WARN_ID:
                logger.warn(message, t);
                break;
            case ERROR_ID:
                logger.error(message, t);
                break;
            default:
                logger.error(message, t);
                break;
        }
    }

    @Override
    public boolean isLevelEnabled(int level) {
        switch (level) {
            case TRACE_ID:
                return logger.isTraceEnabled();
            case DEBUG_ID:
                return logger.isDebugEnabled();
            case INFO_ID:
                return logger.isInfoEnabled();
            case WARN_ID:
                return logger.isWarnEnabled();
        }
        return logger.isErrorEnabled();
    }

}
