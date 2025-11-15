package com.ovvium.services.service.handler;

import com.ovvium.services.model.user.User;
import com.ovvium.services.model.user.event.UserRegisteredEvent;
import com.ovvium.services.service.UserService;
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
public class SendVerificationEmailOnUserRegisteredHandler extends EventHandler<UserRegisteredEvent> {

	private final Environment props;
	private final MailHelper mailHelper;
	private final UserService userService;

	@Override
	public void handle(UserRegisteredEvent event) {
		val user = userService.getUserOrFail(event.getUserId());
		log.info("Send verification Email for registered user (verification pending) {} ", user.getId());
		val baseActivationUrl = props.getRequiredProperty("account-service.verify.url");
		val activationCode = User.generateVerificationHash(user.getId(), user.getEmail(), props.getRequiredProperty("jwt.secret"));
		val activationUrl = baseActivationUrl + "?id=" + user.getId() + "&activationCode=" + activationCode;
		sendActivationEmail(user, activationUrl);
	}

	private void sendActivationEmail(User user, String activationUrl) {
		Map<String, Object> map = Map.of(
				"activationUrl", activationUrl,
				"baseUrl", props.getRequiredProperty("website.baseUrl")
		);
		mailHelper.sendMail(new Mail()
				.setSubject("Verifica tu cuenta de Ovvium")
				.setHtml(VelocityUtils.run("templates/mail/verifyUserMailTemplate", map))
				.addRecipients(user.getEmail()));
	}
}
