package com.ovvium.services.web.controller;

import com.ovvium.services.service.AccountService;
import com.ovvium.services.util.mail.Mail;
import com.ovvium.services.util.mail.MailHelper;
import com.ovvium.services.web.controller.bff.v1.transfer.request.account.VerifyUserRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.staticweb.ContactData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

import static java.lang.String.format;
import static java.util.concurrent.Executors.newSingleThreadExecutor;

@Slf4j
@Controller
@RequiredArgsConstructor
public class StaticWebController {

	private final Environment props;
	private final MailHelper mailHelper;
	private final AccountService accountService;

	@RequestMapping(value = "/requestInfo", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public void requestInfo(ContactData formData, HttpServletResponse response) {
		newSingleThreadExecutor().execute(() -> mailHelper.sendMail(new Mail()//
				.setSubject("Solicitud de información comercial")//
				.setText(formData.toString())//
				.addRecipients(props.getRequiredProperty("mail.to"))));

		response.setStatus(200);
	}

	@RequestMapping(value = "/verify-user")
	public ModelAndView verifyUser(@RequestParam UUID id, @RequestParam String activationCode) {
		val mav = new ModelAndView(format("redirect:%s", props.getRequiredProperty("account-service.verify.redirect.ok")));
		try {
			accountService.verify(new VerifyUserRequest(id, activationCode));
		} catch (Exception e) {
			log.error("Can't verify user: " + id, e);
			new ModelAndView(format("redirect:%s", props.getRequiredProperty("account-service.verify.redirect.error")));
		}
		return mav;
	}
}
