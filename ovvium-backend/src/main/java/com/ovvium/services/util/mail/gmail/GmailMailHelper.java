package com.ovvium.services.util.mail.gmail;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.ovvium.services.app.config.properties.MailProperties;
import com.ovvium.services.util.mail.DefaultMailHelper;
import com.ovvium.services.util.mail.Mail;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.util.Optional;
import java.util.Properties;

import static com.google.api.client.util.Base64.encodeBase64URLSafeString;
import static com.ovvium.services.util.ovvium.base.Preconditions.checkNotNull;

@Slf4j
public class GmailMailHelper extends DefaultMailHelper {

	private final MailProperties.MailFromProperties from;
	private final Credential credentials;

	public GmailMailHelper(boolean enabled, MailProperties.MailFromProperties from, Credential credentials) {
		super(null, enabled, null);
		this.from = enabled ? checkNotNull(from, "From cannot be null") : from;
		this.credentials = enabled ? checkNotNull(credentials, "Credentials can't be null") : credentials;
	}

	@Override
	@SneakyThrows
	public void sendMail(Mail mail) {
		if (enabled) {
			String email = Optional.ofNullable(mail.getFrom()).orElse(from.getEmail());
			createGmail()//
					.users()//
					.messages()//
					.send(email, message(mail))//
					.execute();
		} else {
			log.info("Mail is disabled. Mail to send: {}", mail);
		}
	}

	@SneakyThrows
	private Gmail createGmail() {
		return new Gmail.Builder(credentials.getTransport(), credentials.getJsonFactory(), credentials)//
				.setApplicationName("Ovvium Server")//
				.build();
	}

	@SneakyThrows
	private Message message(Mail mail) {
		val message = new MimeMessage(Session.getDefaultInstance(new Properties(), null));
		String email = Optional.ofNullable(mail.getFrom()).orElse(from.getEmail());
		String personalName = Optional.ofNullable(mail.getPersonalName()).orElse(from.getName());
		message.setFrom(new InternetAddress(email, personalName));
		for (val recipient : mail.getRecipients()) {
			message.addRecipients(CONVERSION_RECIPIENT_KIND_MAP.get(recipient.getKey()), recipient.getValue());
		}
		message.setSubject(mail.getSubject());
		if (mail.getText() != null) {
			message.setText(mail.getText(), "UTF-8");
		} else if (mail.getHtml() != null) {
			message.setText(mail.getHtml(), "UTF-8", "html");
		}
		@Cleanup val buffer = new ByteArrayOutputStream();
		message.writeTo(buffer);
		return new Message().setRaw(encodeBase64URLSafeString(buffer.toByteArray()));
	}
}