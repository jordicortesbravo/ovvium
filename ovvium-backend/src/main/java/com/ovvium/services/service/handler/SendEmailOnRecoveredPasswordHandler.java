package com.ovvium.services.service.handler;

import com.ovvium.services.model.user.User;
import com.ovvium.services.model.user.event.RecoveredPasswordEvent;
import com.ovvium.services.service.UserService;
import com.ovvium.services.service.event.EventHandler;
import com.ovvium.services.util.mail.Mail;
import com.ovvium.services.util.mail.MailHelper;
import com.ovvium.services.util.util.velocity.VelocityUtils;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class SendEmailOnRecoveredPasswordHandler extends EventHandler<RecoveredPasswordEvent> {

	private final Environment props;
	private final UserService userService;
	private final MailHelper mailHelper;

	@Override
	public void handle(RecoveredPasswordEvent event) {
		val user = userService.getUserOrFail(event.getUserId());
		val password = user.generatePassword();
		userService.save(user);
		sendRecoverEmail(user, password);
	}

	private void sendRecoverEmail(User user, String newPassword) {
		val baseUrl = props.getRequiredProperty("website.baseUrl");
		Map<String, Object> map = Map.of(
				"password", newPassword,
				"baseUrl", baseUrl,
				"user", user
		);
		mailHelper.sendMail(new Mail()//
				.setSubject("Tu nueva contraseña")//
				.setHtml(VelocityUtils.run("templates/mail/recoverPasswordMailTemplate", map))//
				.addRecipients(user.getEmail()));
	}

}
