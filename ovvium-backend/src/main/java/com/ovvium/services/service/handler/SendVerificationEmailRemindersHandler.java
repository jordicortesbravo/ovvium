package com.ovvium.services.service.handler;

import com.ovvium.services.model.user.User;
import com.ovvium.services.model.user.event.UserVerificationReminderEvent;
import com.ovvium.services.repository.UserRepository;
import com.ovvium.services.service.event.EventHandler;
import com.ovvium.services.util.mail.Mail;
import com.ovvium.services.util.mail.MailHelper;
import com.ovvium.services.util.util.velocity.VelocityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SendVerificationEmailRemindersHandler extends EventHandler<UserVerificationReminderEvent> {

	private final Environment props;
	private final MailHelper mailHelper;
	private final UserRepository userRepository;

	@Override
	public void handle(UserVerificationReminderEvent event) {
		val notVerifiedEmails = userRepository.getNotVerifiedUserEmails();
		val baseActivationUrl = props.getRequiredProperty("account-service.verify.url");
		val secret = props.getRequiredProperty("jwt.secret");
		notVerifiedEmails.forEach(user -> {
			val activationCode = User.generateVerificationHash(user.getId(), user.getEmail(), secret);
			val activationUrl = baseActivationUrl + "?id=" + user.getId() + "&activationCode=" + activationCode;
			sendActivationEmail(user.getEmail(), activationUrl);
		});
		log.info("Sent {} verification emails", notVerifiedEmails.size());
	}

	private void sendActivationEmail(String email, String activationUrl) {
		Map<String, Object> map = Map.of(
				"activationUrl", activationUrl,
				"baseUrl", props.getRequiredProperty("website.baseUrl")
		);
		try {
			mailHelper.sendMail(new Mail()
					.setSubject("Verifica tu cuenta de Ovvium")
					.setHtml(VelocityUtils.run("templates/mail/verifyUserMailTemplate", map))
					.addRecipients(email));
		} catch (Exception exc) {
			log.error("Sending email to '" + email + "' failed", exc);
		}
	}
}
