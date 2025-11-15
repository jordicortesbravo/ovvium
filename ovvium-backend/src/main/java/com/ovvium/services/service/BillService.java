package com.ovvium.services.service;

import com.ovvium.services.model.bill.Bill;
import com.ovvium.services.transfer.command.bill.CreateOrJoinBillCommand;
import com.ovvium.services.util.ovvium.cache.CollectionWrapper;
import com.ovvium.services.web.controller.bff.v1.transfer.request.bill.JoinBillAndLocationsRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.bill.UpdateBillRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.order.CreateOrderRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.response.bill.BillResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.common.ResourceIdResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.order.OrderResponse;

import java.util.List;
import java.util.UUID;

public interface BillService {

	Bill createOrJoin(CreateOrJoinBillCommand command);

	Bill joinBillAndLocations(JoinBillAndLocationsRequest joinBillAndLocationsRequest);

	void deleteOrder(UUID billId, UUID order);

	Bill getBill(UUID billId);

	void updateBill(UpdateBillRequest updateBillRequest);

	void closeBill(Bill bill);

	ResourceIdResponse addOrder(UUID billId, CreateOrderRequest createOrderRequest);

	List<ResourceIdResponse> addOrders(UUID billId, List<CreateOrderRequest> createOrdersRequest);

	List<ResourceIdResponse> addOrdersToCurrentBill(List<CreateOrderRequest> createOrdersRequest);

	List<OrderResponse> getOrders(UUID billId, String issueStatus);

	OrderResponse getOrder(UUID billId, UUID orderId);

	CollectionWrapper<BillResponse> listOpenBills(UUID customerId);

	Bill getCurrentBillOfUser(UUID userId);

	BillResponse getCurrentBillOfUser();

	BillResponse getBillResponse(UUID billId);

	Bill save(Bill bill);
}
