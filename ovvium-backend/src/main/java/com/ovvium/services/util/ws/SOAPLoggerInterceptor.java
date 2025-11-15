package com.ovvium.services.util.ws;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.apache.cxf.interceptor.AbstractLoggingInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.io.CacheAndWriteOutputStream;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.io.CachedOutputStreamCallback;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;

import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SOAPLoggerInterceptor extends AbstractLoggingInterceptor {

	private class LoggingCallback implements CachedOutputStreamCallback {
		public void onFlush(CachedOutputStream cos) {
		}

		public void onClose(CachedOutputStream cos) {
			try {
				StringBuilder builder = new StringBuilder();
				cos.writeCacheTo(builder, limit);
				String soapXml = builder.toString();
				log.debug(soapXml);
			} catch (Exception e) {
			}
		}

	}

	private SOAPLoggerInterceptor(String phase) {
		super(phase);
	}

	@Override
	public void handleMessage(Message message) throws Fault {
		try {
			if (getPhase().equals(Phase.PRE_STREAM)) {
				OutputStream out = message.getContent(OutputStream.class);
				CacheAndWriteOutputStream newOut = new CacheAndWriteOutputStream(out);
				message.setContent(OutputStream.class, newOut);
				newOut.registerCallback(new LoggingCallback());
			} else {
				@Cleanup
				InputStream is = message.getContent(InputStream.class);
				@Cleanup
				CachedOutputStream os = new CachedOutputStream();
				IOUtils.copy(is, os);
				os.flush();
				message.setContent(InputStream.class, os.getInputStream());
				log.debug(IOUtils.toString(os.getInputStream()));
			}
		} catch (Exception e) {
			log.debug("Error logging SOAP {} {}", getPhase(), e);
		}
	}

	public static SOAPLoggerInterceptor getInInterceptor() {
		return new SOAPLoggerInterceptor(Phase.RECEIVE);
	}

	public static SOAPLoggerInterceptor getOutInterceptor() {
		return new SOAPLoggerInterceptor(Phase.PRE_STREAM);
	}

	@Override
	protected Logger getLogger() {
		return null;
	}


}