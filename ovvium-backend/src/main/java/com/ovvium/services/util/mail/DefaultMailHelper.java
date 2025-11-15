package com.ovvium.services.util.mail;

import com.ovvium.services.util.util.basic.Utils;
import com.ovvium.services.util.util.container.Maps;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message.RecipientType;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("deprecation")
public class DefaultMailHelper implements MailHelper {

	protected static final Map<RecipientKind, RecipientType> CONVERSION_RECIPIENT_KIND_MAP = Maps
			.map(RecipientKind.class, RecipientType.class)//
			.with(RecipientKind.TO, RecipientType.TO) //
			.with(RecipientKind.CC, RecipientType.CC) //
			.with(RecipientKind.BCC, RecipientType.BCC);

	private final JavaMailSender sender;

	protected final boolean enabled;
	protected final String defaultTo;

	@Override
	@SneakyThrows
	public void sendMail(Mail mail) {
		if (!enabled) {
			return;
		}

		val message = sender.createMimeMessage();
		val msg = new MimeMessageHelper(message, mail.isMultipart(), "UTF-8");

		msg.setFrom(mail.getFrom());
		msg.setSubject(mail.getSubject());
		if (mail.getHtml() != null) {
			msg.setText(mail.getHtml(), true);
		}
		if (mail.getText() != null) {
			msg.setText(mail.getText(), false);
		}
		for (val header : mail.getHeaders()) {
			message.addHeader(header.getKey(), header.getValue());
		}

		for (val recipient : mail.getRecipients()) {
			String[] addresses = Utils.firstNonNull(defaultTo, "").split(";|,");
			if (addresses.length > 0) {
				for (val address : addresses) {
					message.addRecipients(CONVERSION_RECIPIENT_KIND_MAP.get(recipient.getKey()), address);
				}
			} else {
				message.addRecipients(CONVERSION_RECIPIENT_KIND_MAP.get(recipient.getKey()), recipient.getValue());
			}
		}

		for (val res : mail.getResources()) {
			val content = new ByteArrayResource(res.getContent());
			if (res.getType() == MailResourceType.ATTACHMENT) {
				msg.addAttachment(res.getName(), content);
			} else {
				String contentType = msg.getFileTypeMap().getContentType(res.getName());
				msg.addInline(res.getName(), content, contentType);
			}
		}

		if (!mail.getCalendars().isEmpty()) {
			for (val cal : mail.getCalendars()) {
				Multipart multipart = new MimeMultipart();
				BodyPart body = new MimeBodyPart();
				body.addHeader("Content-Class", "urn:content-classes:calendarmessage");
				body.addHeader("Content-ID", "calendar_message");
				body.setContent(cal.toString(), "text/Calendar;method=" + cal.getMethod());
				multipart.addBodyPart(body);
				msg.getMimeMessage().setContent(multipart);
			}
		}

		Address[] recipients = message.getRecipients(RecipientType.TO);
		if (recipients == null || recipients.length == 0) {
			return;
		}
		try {
			sender.send(message);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

}
