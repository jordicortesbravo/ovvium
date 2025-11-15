package com.ovvium.services.web.controller.bff.v1;

import com.ovvium.services.service.PaymentService;
import com.ovvium.services.service.application.PaymentsApplicationService;
import com.ovvium.services.web.controller.bff.v1.transfer.request.payment.AddCardTokenRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.payment.AdvancePaymentAppCardRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.payment.PaymentAppCardRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.payment.PaymentInvoiceRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.response.common.ResourceIdResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.payment.PaymentOrderAppCardResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.payment.PaymentOrderResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.payment.UserCardDataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.ovvium.services.model.payment.PaymentType.CARD;
import static com.ovvium.services.model.payment.PaymentType.CASH;
import static com.ovvium.services.web.controller.bff.v1.WebConstants.BASE_URI_API_V1;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = BASE_URI_API_V1, produces = APPLICATION_JSON_VALUE)
public class PaymentApiController {

	private final PaymentService paymentService;
	private final PaymentsApplicationService applicationService;

	@PostMapping("/payments/app-card")
	@ResponseStatus(CREATED)
	@PreAuthorize("hasRole('USERS')")
	public PaymentOrderAppCardResponse createAppCardPayment(@RequestBody PaymentAppCardRequest request) {
		return paymentService.pay(request);
	}

	@GetMapping("/payments/{id}")
	@ResponseStatus(OK)
	@PreAuthorize("hasAnyRole('USERS', 'CUSTOMERS_ADMIN', 'CUSTOMERS_USER')")
	public PaymentOrderResponse getPaymentOrder(@PathVariable UUID id) {
		return applicationService.getPaymentOrder(id);
	}

	@PostMapping("/payments/advance-app-card")
	@ResponseStatus(CREATED)
	@PreAuthorize("hasRole('USERS')")
	public PaymentOrderAppCardResponse createAppCardAdvancePayment(@RequestBody AdvancePaymentAppCardRequest request) {
		return applicationService.payAndOrder(request);
	}

	//TODO Return PaymentOrderAppCardResponse
	@PostMapping("/payments/cash")
	@ResponseStatus(CREATED)
	@PreAuthorize("hasAnyRole('CUSTOMERS_ADMIN', 'CUSTOMERS_USER')")
	public ResourceIdResponse createCashPayment(@RequestBody PaymentInvoiceRequest request) {
		return paymentService.pay(request.setType(CASH));
	}

	//TODO Return PaymentOrderAppCardResponse
	@PostMapping("/payments/card")
	@ResponseStatus(CREATED)
	@PreAuthorize("hasAnyRole('CUSTOMERS_ADMIN', 'CUSTOMERS_USER')")
	public ResourceIdResponse createCardPayment(@RequestBody PaymentInvoiceRequest request) {
		return paymentService.pay(request.setType(CARD));
	}

	@PostMapping("/me/card-token")
	@ResponseStatus(CREATED)
	@PreAuthorize("hasRole('USERS')")
	public ResourceIdResponse addCardToken(@RequestBody AddCardTokenRequest request){
		return paymentService.addCardToken(request);
	}

	@DeleteMapping("/me/card-token/{pciDetailsId}")
	@ResponseStatus(OK)
	@PreAuthorize("hasRole('USERS')")
	public void removeCardToken(@PathVariable UUID pciDetailsId){
		paymentService.removeCardToken(pciDetailsId);
	}

	@GetMapping("/me/cards")
	@ResponseStatus(OK)
	@PreAuthorize("hasRole('USERS')")
	public List<UserCardDataResponse> getCardsOfCurrentUser(){
		return paymentService.getCardsOfCurrentUser();
	}

}
