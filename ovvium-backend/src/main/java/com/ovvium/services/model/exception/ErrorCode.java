package com.ovvium.services.model.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * ErrorCodes for Domain Specific Errors. ErrorCodes are created in ranges. For
 * example, 1000-1999 for Ratings and so on.
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

	// ratings
	RATING_ALREADY_EXISTS(1000, "Rating already exists for this product and user."),
	// bill
	BILL_ALREADY_OPENED_FOR_LOCATION(2000, "There is already an opened bill for this location."),
	BILL_HAS_PENDING_ORDERS(2001, "There are pending orders to be paid for this bill."),
	BILL_ALREADY_OPENED_FOR_USER(2002, "There is already an opened bill for this User."),
	BILL_INVOICE_CANNOT_BE_DELETED(2003, "There are Invoices referencing this Bill. This bill cannot be deleted."),
	BILL_PAYMENT_CANNOT_BE_DELETED(2004, "There are PaymentOrders referencing this Bill. This bill cannot be deleted."),
	//orders
	ORDER_CANNOT_BE_ADDED_TO_CLOSED_BILL(3000, "Orders cannot be added to a closed Bill."),
	ORDER_CANNOT_BE_DELETED(3001, "Order is not PENDING. Order cannot be deleted "),
	// payments
	ORDER_ALREADY_PAID(4000, "Order is already paid."),
	PAYMENT_SPLIT_AMOUNT_IS_NOT_CORRECT(4001, "The amount to split is less than the Ovvium commission."),
	ORDER_IS_BEING_PAID(4002, "Order is being paid, another user is paying this order."),
	PAYMENT_ORDER_NOT_EXECUTED(4003, "Payment Order has no executed purchase details."),
	PAYMENT_ORDER_ALREADY_SPLIT(4004, "Payment Order was already splitted."),
	//invoices
	INVOICE_DATE_RECENT_EXISTS(5000, "It already exists a more recent Invoice Date."),
	INVOICE_DATE_EXISTS(5001, "Invoice Date for this date already exists."),
	INVOICE_DATE_OPEN_EXISTS(5002, "Invoice Date for another date is not closed."),
	INVOICE_DATE_NOT_OPENED(5003, "There is no Invoice Date opened for this Customer."),
	INVOICE_ALREADY_PAID(5004, "This Invoice has been already paid."),
	INVOICE_CANNOT_BE_MODIFIED(5005, "This Invoice cannot be modified."),
	INVOICE_DATE_LAST_NOT_CLOSED(5006, "The most recent Invoice Date is not closed, cannot open this invoice date."),
	INVOICE_DATE_BILLS_OPEN(5007, "Bills still open for Invoice Date, cannot close this invoice date."),
	// user
	USER_ALREADY_EXISTS(6001, "The user that was trying to register, already exists."),
	PASSWORD_TOO_SHORT(6002, "The password must have at least 8 characters"),
	USER_NOT_REMOVABLE_BILL_OPEN(6003, "User cannot be removed as there are Bills opened for this user."),
	// customer
	CUSTOMER_EMPLOYEE_NOT_FOUND(7000, "This employee code is not found on this Customer."),
	// products
	PRODUCT_GROUP_TIMES_NOT_CORRECT(8000, "The start time and end time for this Product are not correct."),
	PRODUCT_PRICE_TOO_LOW(8001, "The Product price is lower than the commission.");

	private final int errorCode;
	private final String message;

}
