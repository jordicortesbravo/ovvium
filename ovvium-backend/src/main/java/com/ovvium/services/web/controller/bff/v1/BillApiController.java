package com.ovvium.services.web.controller.bff.v1;

import com.ovvium.services.service.BillService;
import com.ovvium.services.service.application.BillApplicationService;
import com.ovvium.services.web.controller.bff.v1.transfer.request.bill.CreateOrJoinBillRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.bill.JoinBillAndLocationsRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.bill.UpdateBillRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.order.CreateOrderRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.response.bill.BillResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.common.ResourceIdResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.order.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.ovvium.services.web.controller.bff.v1.WebConstants.BASE_URI_API_V1;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = BASE_URI_API_V1, produces = APPLICATION_JSON_VALUE)
public class BillApiController {

	private final BillApplicationService applicationService;
	private final BillService billService;

	// This endpoint is really coupled with PoS
	@ResponseStatus(OK)
	@GetMapping("/customers/{customerId}/bills")
	@PreAuthorize("hasAnyRole('CUSTOMERS_ADMIN', 'CUSTOMERS_USER') and @auth.isFromCustomer(#customerId)")
	public List<BillResponse> listOpenBills(@PathVariable UUID customerId) {
		return billService.listOpenBills(customerId).toList();
	}

	@ResponseStatus(CREATED)
	@PostMapping("/customers/{customerId}/bills")
	public ResourceIdResponse createOrJoinToBill(@PathVariable UUID customerId,
												 @RequestBody CreateOrJoinBillRequest request) {
		request.setCustomerId(customerId);
		return applicationService.createOrJoinToBill(request);
	}

	@ResponseStatus(OK)
	@PostMapping("/customers/{customerId}/bills/{destinationBillId}/join")
	@PreAuthorize("hasAnyRole('CUSTOMERS_ADMIN', 'CUSTOMERS_USER') and @auth.isFromCustomer(#customerId)")
	public void joinBillsOrLocations(@PathVariable UUID customerId, @PathVariable UUID destinationBillId,
									 @RequestBody JoinBillAndLocationsRequest joinBillAndLocationsRequest) {
		joinBillAndLocationsRequest.setCustomerId(customerId);
		joinBillAndLocationsRequest.setDestinationBillId(destinationBillId);
		billService.joinBillAndLocations(joinBillAndLocationsRequest);
	}

	@ResponseStatus(OK)
	@GetMapping("/customers/{customerId}/bills/{billId}")
	@PreAuthorize("hasAnyRole('CUSTOMERS_ADMIN', 'CUSTOMERS_USER') and @auth.isFromCustomer(#customerId)")
	public BillResponse getBill(@PathVariable UUID customerId, @PathVariable UUID billId) {
		return billService.getBillResponse(billId);
	}

	@ResponseStatus(OK)
	@PatchMapping("/customers/{customerId}/bills/{billId}")
	@PreAuthorize("hasAnyRole('CUSTOMERS_ADMIN', 'CUSTOMERS_USER') and @auth.isFromCustomer(#customerId)")
	public void updateBill(@PathVariable UUID customerId, @PathVariable UUID billId, @RequestBody UpdateBillRequest updateBillRequest) {
		billService.updateBill(updateBillRequest.setBillId(billId).setCustomerId(customerId));
	}

	@ResponseStatus(OK)
	@DeleteMapping("/customers/{customerId}/bills/{billId}")
	@PreAuthorize("hasAnyRole('CUSTOMERS_ADMIN', 'CUSTOMERS_USER') and @auth.isFromCustomer(#customerId)")
	public void closeBill(@PathVariable UUID customerId, @PathVariable UUID billId) {
		applicationService.closeBill(customerId, billId);
	}

	@ResponseStatus(OK)
	@GetMapping("/customers/{customerId}/bills/{billId}/orders")
	public List<OrderResponse> getOrders(@PathVariable UUID billId, @RequestParam(required = false) String issueStatus, @PathVariable String customerId) {
		return billService.getOrders(billId, issueStatus);
	}

	@ResponseStatus(OK)
	@GetMapping("/customers/{customerId}/bills/{billId}/orders/{orderId}")
	public OrderResponse getOrder(@PathVariable UUID billId, @PathVariable UUID orderId, @PathVariable String customerId) {
		return billService.getOrder(billId, orderId);
	}

	@ResponseStatus(CREATED)
	@PostMapping("/customers/{customerId}/bills/{billId}/orders")
	@PreAuthorize("hasAnyRole('CUSTOMERS_ADMIN', 'CUSTOMERS_USER') and @auth.isFromCustomer(#customerId)")
	public ResourceIdResponse addSingleOrder(@PathVariable UUID customerId, @PathVariable UUID billId, @RequestBody CreateOrderRequest createOrderRequest) {
		return billService.addOrder(billId, createOrderRequest);
	}

	@ResponseStatus(CREATED)
	@PostMapping("/customers/{customerId}/bills/{billId}/orders/bulk")
	@PreAuthorize("hasAnyRole('CUSTOMERS_ADMIN', 'CUSTOMERS_USER') and @auth.isFromCustomer(#customerId)")
	public List<ResourceIdResponse> addMultipleOrders(@PathVariable UUID customerId, @PathVariable UUID billId, @RequestBody List<CreateOrderRequest> createOrdersRequest) {
		return billService.addOrders(billId, createOrdersRequest);
	}

	@ResponseStatus(OK)
	@DeleteMapping("/customers/{customerId}/bills/{billId}/orders/{orderId}")
	@PreAuthorize("hasAnyRole('CUSTOMERS_ADMIN', 'CUSTOMERS_USER') and @auth.isFromCustomer(#customerId)")
	public void deleteOrder(@PathVariable UUID customerId, @PathVariable UUID billId, @PathVariable UUID orderId) {
		billService.deleteOrder(billId, orderId);
	}

	@ResponseStatus(OK)
	@GetMapping("/me/bill")
	@PreAuthorize("hasRole('USERS')")
	public BillResponse getMyCurrentBill() {
		return billService.getCurrentBillOfUser();
	}

	@ResponseStatus(OK)
	@PostMapping("/me/bill")
	@PreAuthorize("hasRole('USERS')")
	public ResourceIdResponse createOrJoinCurrentUserToBill(@RequestBody CreateOrJoinBillRequest createOrJoinBillRequest) {
		return applicationService.createOrJoinCurrentUserToBill(createOrJoinBillRequest);
	}

	@ResponseStatus(CREATED)
	@PostMapping("/me/bill/orders")
	@PreAuthorize("hasRole('USERS')")
	public List<ResourceIdResponse> addOrdersToCurrentBill(@RequestBody List<CreateOrderRequest> createOrdersRequest) {
		return billService.addOrdersToCurrentBill(createOrdersRequest);
	}
}
