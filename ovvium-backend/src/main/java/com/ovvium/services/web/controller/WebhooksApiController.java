package com.ovvium.services.web.controller;

import com.ovvium.services.service.application.WebhooksApplicationService;
import com.ovvium.services.web.controller.bff.v1.transfer.request.webhooks.paycomet.PaycometWebhookRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/webhooks", produces = APPLICATION_JSON_VALUE)
public class WebhooksApiController {

	private final WebhooksApplicationService service;

	@PostMapping(value = "/payments/pc", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	@ResponseStatus(OK)
	public void paycometWebhookListener(PaycometWebhookRequest request)  {
		service.handlePaycometNotification(request);
	}

}
