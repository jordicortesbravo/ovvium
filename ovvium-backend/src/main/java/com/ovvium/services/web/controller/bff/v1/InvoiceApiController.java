package com.ovvium.services.web.controller.bff.v1;

import com.ovvium.services.service.InvoiceDateService;
import com.ovvium.services.service.InvoiceService;
import com.ovvium.services.web.controller.bff.v1.transfer.request.common.GetPageRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.invoice.*;
import com.ovvium.services.web.controller.bff.v1.transfer.response.common.ResourceIdResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.invoice.InvoiceDatePageResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.invoice.InvoiceDateResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.invoice.InvoicePageResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.invoice.InvoiceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.ovvium.services.web.controller.bff.v1.WebConstants.BASE_URI_API_V1;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = BASE_URI_API_V1, produces = APPLICATION_JSON_VALUE)
public class InvoiceApiController {

	private final InvoiceService invoiceService;
	private final InvoiceDateService invoiceDateService;

	@PostMapping("/customers/{customerId}/invoice-dates")
	@ResponseStatus(CREATED)
	@PreAuthorize("hasAnyRole('CUSTOMERS_ADMIN', 'CUSTOMERS_USER') and @auth.isFromCustomer(#customerId)")
	public ResourceIdResponse createInvoiceDate(@PathVariable UUID customerId, @RequestBody CreateInvoiceDateRequest request) {
		return invoiceDateService.createInvoiceDate(request.setCustomerId(customerId));
	}


	@PatchMapping("/customers/{customerId}/invoice-dates/{invoiceDateId}")
	@ResponseStatus(OK)
	@PreAuthorize("hasAnyRole('CUSTOMERS_ADMIN', 'CUSTOMERS_USER') and @auth.isFromCustomer(#customerId)")
	public void updateInvoiceDate(@PathVariable UUID customerId, @PathVariable UUID invoiceDateId, @RequestBody UpdateInvoiceDateRequest request) {
		invoiceDateService.updateInvoiceDate(request.setCustomerId(customerId).setInvoiceDateId(invoiceDateId));
	}

	@GetMapping("/customers/{customerId}/invoice-dates")
	@ResponseStatus(OK)
	@PreAuthorize("hasAnyRole('CUSTOMERS_ADMIN', 'CUSTOMERS_USER') and @auth.isFromCustomer(#customerId)")
	public InvoiceDatePageResponse listInvoiceDates(@PathVariable UUID customerId, @RequestParam("month") int month, @RequestParam("year") int year) {
		return invoiceDateService.pageInvoiceDates(customerId, month, year);
	}

	@GetMapping("/customers/{customerId}/invoice-dates/last")
	@ResponseStatus(OK)
	@PreAuthorize("hasAnyRole('CUSTOMERS_ADMIN', 'CUSTOMERS_USER') and @auth.isFromCustomer(#customerId)")
	public InvoiceDateResponse getLastInvoiceDate(@PathVariable UUID customerId) {
		return invoiceDateService.getLastInvoiceDate(customerId);
	}

	@PostMapping("/customers/{customerId}/invoices")
	@ResponseStatus(CREATED)
	@PreAuthorize("hasAnyRole('CUSTOMERS_ADMIN', 'CUSTOMERS_USER') and @auth.isFromCustomer(#customerId)")
	public ResourceIdResponse createInvoiceDraft(@PathVariable UUID customerId, @RequestBody CreateInvoiceDraftRequest request) {
		return invoiceService.createInvoiceDraft(request.setCustomerId(customerId));
	}

	@GetMapping("/customers/{customerId}/invoices")
	@ResponseStatus(OK)
	@PreAuthorize("hasAnyRole('CUSTOMERS_ADMIN', 'CUSTOMERS_USER')")
	public InvoicePageResponse listInvoicesOfCustomer(@PathVariable UUID customerId,
													  @RequestParam(required = false) Integer page,
													  @RequestParam(required = false) Integer size,
													  @RequestParam(required = false) String invoiceDate) {
		GetInvoiceCustomerPageRequest request = new GetInvoiceCustomerPageRequest(page, size, customerId, invoiceDate);
		return invoiceService.getInvoicesOfCustomer(request);
	}

	@PostMapping("/customers/{customerId}/invoices/{invoiceId}/orders")
	@ResponseStatus(OK)
	@PreAuthorize("hasAnyRole('CUSTOMERS_ADMIN', 'CUSTOMERS_USER') and @auth.isFromCustomer(#customerId)")
	public void addOrdersToInvoice(@PathVariable UUID customerId, @PathVariable UUID invoiceId, @RequestBody CreateInvoiceOrdersRequest request) {
		invoiceService.addOrdersToInvoice(request.setCustomerId(customerId).setInvoiceId(invoiceId));
	}

	@DeleteMapping("/customers/{customerId}/invoices/{invoiceId}/orders/{orderId}")
	@ResponseStatus(OK)
	@PreAuthorize("hasAnyRole('CUSTOMERS_ADMIN', 'CUSTOMERS_USER') and @auth.isFromCustomer(#customerId)")
	public void removeOrderFromInvoice(@PathVariable UUID customerId, @PathVariable UUID invoiceId, @PathVariable UUID orderId) {
		invoiceService.removeOrderFromInvoice(new DeleteInvoiceOrdersRequest()
				.setCustomerId(customerId)
				.setInvoiceId(invoiceId)
				.setOrderId(orderId)
		);
	}

	@GetMapping("/customers/{customerId}/invoices/{invoiceId}")
	@ResponseStatus(OK)
	@PreAuthorize("hasAnyRole('CUSTOMERS_ADMIN', 'CUSTOMERS_USER') and @auth.isFromCustomer(#customerId)")
	@PostAuthorize("@auth.isFromCustomer(returnObject.customer.id)")
	public InvoiceResponse getInvoice(@PathVariable UUID customerId, @PathVariable UUID invoiceId) {
		return invoiceService.getInvoiceResponse(invoiceId);
	}

	@GetMapping("/me/invoices/{invoiceId}")
	@ResponseStatus(OK)
	@PreAuthorize("hasRole('USERS')")
	@PostAuthorize("@auth.isSameUser(returnObject.user.id)")
	public InvoiceResponse getInvoiceOfCurrentUser(@PathVariable UUID invoiceId) {
		return invoiceService.getInvoiceResponse(invoiceId);
	}

	@GetMapping("/me/invoices")
	@ResponseStatus(OK)
	@PreAuthorize("hasRole('USERS')")
	public InvoicePageResponse getInvoicesOfCurrentUser(@RequestParam(required = false) Integer page,
														@RequestParam(required = false) Integer size) {
		return invoiceService.getInvoicesOfCurrentUser(new GetPageRequest(page, size));
	}

}
