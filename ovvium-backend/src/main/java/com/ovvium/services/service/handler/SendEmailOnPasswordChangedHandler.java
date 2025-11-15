package com.ovvium.services.service.handler;

import com.ovvium.services.model.user.event.UserPasswordChangedEvent;
import com.ovvium.services.service.UserService;
import com.ovvium.services.service.event.EventHandler;
import com.ovvium.services.util.mail.Mail;
import com.ovvium.services.util.mail.MailHelper;
import com.ovvium.services.util.util.velocity.VelocityUtils;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SendEmailOnPasswordChangedHandler extends EventHandler<UserPasswordChangedEvent> {

	private final UserService userService;
	private final MailHelper mailHelper;

	private final String baseUrl;

	@Autowired
	public SendEmailOnPasswordChangedHandler(UserService userService, MailHelper mailHelper, Environment props) {
		this.userService = userService;
		this.mailHelper = mailHelper;
		this.baseUrl = props.getRequiredProperty("website.baseUrl");
	}

	@Override
	public void handle(UserPasswordChangedEvent event) {
		val user = userService.getUserOrFail(event.getUserId());
		val map = Map.<String, Object>of(
				"user", user.getName(),
				"baseUrl", baseUrl
		);
		mailHelper.sendMail(new Mail()//
				.setSubject("Tu contraseña ha cambiado")//
				.setHtml(VelocityUtils.run("templates/mail/changedPasswordMailTemplate", map))//
				.addRecipients(user.getEmail()));
	}

}
