package com.ovvium.services.service.impl;

import com.ovvium.services.app.config.properties.MailProperties;
import com.ovvium.services.service.MailService;
import com.ovvium.services.util.mail.Mail;
import com.ovvium.services.util.mail.MailHelper;
import com.ovvium.services.util.ovvium.spring.SpringRequestUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

	private final MailHelper mailHelper;
	private final MailProperties props;

	@Override
	public void notifyError(String subject, Exception exc) {
		mailHelper.sendMail(new Mail()
				.setFrom(props.getFrom().getEmail())
				.setPersonalName(props.getFrom().getName())
				.setSubject(subject)
				.addRecipients(props.getTo().toArray(new String[0]))
				.setHtml(String.format("Error on path %s, with StackTrace:<br> %s",
						SpringRequestUtils.getRequestPath().orElse(null),
						ExceptionUtils.getFullStackTrace(exc))));
	}
}
