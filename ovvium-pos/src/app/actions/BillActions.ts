import { properties } from 'app/config/Properties';
import { AppRoute } from 'app/containers/Router/AppRoute';
import { BillSplit } from 'app/model/BillSplit';
import { Customer } from 'app/model/Customer';
import { DomainStatus } from 'app/model/enum/DomainStatus';
import { PaymentMethodType } from 'app/model/enum/PaymentMethodType';
import { Invoice } from 'app/model/Invoice';
import { Location } from 'app/model/Location';
import { OvviumError } from 'app/model/OvviumError';
import { InvoiceResponse } from 'app/model/response/InvoiceResponse';
import axios from 'axios';
import { Dispatch } from 'react';
import { AnyAction } from 'redux';
import { Bill } from '../model/Bill';
import { Order } from '../model/Order';
import { BillResponse } from '../model/response/BillResponse';
import { ResourceIdResponse } from '../model/response/ResourceIdResponse';
import { ArrayUtils } from '../utils/ArrayUtils';
import { createAction, withBaseUrl } from './BaseAction';
import { ExecutionActionType } from './ExecutionActions';
import { OrderResponse } from 'app/model/response/OrderResponse';
import { CreateOrderRequest } from './../model/request/CreateOrderRequest';

export enum BillActionType {
  CREATE_OR_JOIN_BILL = "CREATE_OR_JOIN_BILL",
  DELETE_BILL = "DELETE_BILL",
  SELECT_BILL = "SELECT_BILL",
  LOAD_BILLS = "LOAD_BILLS",
  REFRESH_BILL = "REFRESH_BILL",
  JOIN_BILLS = 'JOIN_BILLS',
  CREATE_ORDER = "CREATE_ORDER",
  REMOVE_ORDER = "REMOVE_ORDER",
  CHARGE = "CHARGE",
  UPDATE_ORDERS = "UPDATE_ORDERS",
  SET_CUSTOMER = "SET_CUSTOMER",
  CREATE_BILL_SPLIT = "CREATE_BILL_SPLIT",
  SELECT_BILL_SPLIT = "SELECT_BILL_SPLIT",
  ADD_ORDER_TO_BILL_SPLIT = "ADD_ORDER_TO_BILL_SPLIT",
  REMOVE_ORDER_FROM_BILL_SPLIT = "REMOVE_ORDER_FROM_BILL_SPLIT",
  REMOVE_BILL_SPLIT = "REMOVE_BILL_SPLIT",
  CREATE_INVOICE_DRAFT = "CREATE_INVOICE_DRAFT"
}

export const loadBillsActionCreator = (customer: Customer) => async (dispatch: Dispatch<AnyAction>) => {
  var url = withBaseUrl(properties.bill.list)
    .replace("{customerId}", customer.id.toString());
  try {
    var billResponses = await axios.get<Array<BillResponse>>(url);
    dispatch(createAction(BillActionType.LOAD_BILLS, billResponses.data.map(br => Bill.from(br))));
  } catch (error) {
    dispatch(createAction(ExecutionActionType.ADD_ERROR, { error, container: AppRoute.BILL }));
    throw new OvviumError(error);
  }
}

export const createOrJoinBillActionCreator = (customer: Customer, locations: Array<Location>) => async (dispatch: Dispatch<AnyAction>) => {
  var url = withBaseUrl(properties.bill.create.replace("{customerId}", customer.id.toString()));
  try {
    var request = {
      customerId: customer.id,
      locationIds: locations.map(l => l.id)
    };
    var response = await axios.post<ResourceIdResponse>(url, request);
    url = withBaseUrl(properties.bill.get
      .replace("{customerId}", customer.id.toString())
      .replace("{billId}", response.data.id));
    var billResponse = await axios.get<BillResponse>(url);
    var bill = Bill.from(billResponse.data);
    dispatch(createAction(BillActionType.CREATE_OR_JOIN_BILL, bill));
    return bill;

  } catch (error) {
    dispatch(createAction(ExecutionActionType.ADD_ERROR, { error, container: AppRoute.LOCATIONS }));
    throw new OvviumError(error);
  }
}

export const refreshBillActionCreator = (bill: Bill, customer: Customer) => async (dispatch: Dispatch<AnyAction>) => {
  var url = withBaseUrl(properties.bill.get//
    .replace("{customerId}", customer.id.toString())//
    .replace("{billId}", bill.id));
  var splits = bill.splits;
  var billResponse = await axios.get<BillResponse>(url);
  var refreshedBill = Bill.from(billResponse.data);
  refreshedBill.splits = splits;
  dispatch(createAction(BillActionType.REFRESH_BILL, refreshedBill));
}

export const updateOrdersActionCreator = (customer: Customer, bill: Bill, orders: Array<Order>) => async (dispatch: Dispatch<AnyAction>) => {
  if (ArrayUtils.isEmpty(orders)) {
    return;
  }
  var url = withBaseUrl(properties.bill.update
    .replace("{customerId}", customer.id.toString())
    .replace("{billId}", bill.id));

  try {
    var request = {
      customerId: customer.id,
      orders: orders.map(o => {
        return {
          orderId: o.id,
          serviceTime: o.serviceTime,
          issueStatus: o.issueStatus,
          notes: o.notes,
          groupChoices: o.groupChoices?.map(gc => {
            return {
              orderGroupChoiceId: gc.id,
              productId: gc.product.id,
              issueStatus: gc.issueStatus,
              notes: gc.notes
            }
          })
        }
      })
    };
    await axios.patch(url, request);
    dispatch(createAction(BillActionType.UPDATE_ORDERS, orders));

  } catch (error) {
    //FIXME Improve this
    dispatch(createAction(ExecutionActionType.ADD_ERROR, { error, container: AppRoute.BILL }));
    dispatch(createAction(ExecutionActionType.ADD_ERROR, { error, container: AppRoute.KITCHEN }));
    throw new OvviumError(error);
  }
}

export const chargeActionCreator = (invoice: Invoice, paymentType: PaymentMethodType) => async (dispatch: Dispatch<AnyAction>) => {
  var url = withBaseUrl(paymentType == PaymentMethodType.CASH ? properties.payment.cash : properties.payment.card);

  try {
    var request = {
      invoiceId: invoice.id,
      type: paymentType
    };
    await axios.post(url, request);
    dispatch(createAction(BillActionType.CHARGE, invoice));

  } catch (error) {
    dispatch(createAction(ExecutionActionType.ADD_ERROR, { error, container: AppRoute.BILL }));
    throw new OvviumError(error);
  }
}

export const selectBillActionCreator = (bill: Bill) => async (dispatch: Dispatch<AnyAction>) => {
  dispatch(createAction(BillActionType.SELECT_BILL, bill));
}

export const createOrderActionCreator = (request: CreateOrderRequest, bill: Bill, customer: Customer) => async (dispatch: Dispatch<AnyAction>) => {
  let url = withBaseUrl(properties.bill.order.create//
    .replace("{customerId}", customer.id.toString())//
    .replace("{billId}", bill.id));
  try {
    let resourceIdResponse = await axios.post<ResourceIdResponse>(url, request);
    let orderResponse = await getOrderResponse(customer, bill, resourceIdResponse.data.id);
    dispatch(createAction(BillActionType.CREATE_ORDER, Order.from(orderResponse.data)));
    refreshBillActionCreator(bill, customer)(dispatch);
  } catch (error) {
    dispatch(createAction(ExecutionActionType.ADD_ERROR, { error, container: AppRoute.TAKE_ORDER }));
    throw new OvviumError(error);
  }
}

export const removeOrderActionCreator = (order: Order, bill: Bill, customer: Customer) => async (dispatch: Dispatch<AnyAction>) => {
  var url = withBaseUrl(properties.bill.order.remove//
    .replace("{customerId}", customer.id.toString())//
    .replace("{billId}", bill.id))
    .replace("{orderId}", order.id!);
  try {
    await axios.delete(url);
    order.domainStatus = DomainStatus.DELETED;
    dispatch(createAction(BillActionType.REMOVE_ORDER, order));
    refreshBillActionCreator(bill, customer)(dispatch);
  } catch (error) {
    dispatch(createAction(ExecutionActionType.ADD_ERROR, { error, container: AppRoute.BILL }));
    throw new OvviumError(error);
  }
}

export const joinBillsAndLocationsActionCreator = (customer: Customer, locations: Array<Location>, bill?: Bill) => async (dispatch: Dispatch<AnyAction>) => {

  if (!bill) {
    createOrJoinBillActionCreator(customer, locations);
  } else {
    try {
      var locationsUrl = withBaseUrl(properties.bill.join
        .replace('{customerId}', customer.id.toString())
        .replace('{billId}', bill.id)
      );

      var request = {
        billId: bill.id,
        locationIds: locations.map(l => l.id)
      };
      await axios.post<void>(locationsUrl, request);
      loadBillsActionCreator(customer);
      var url = withBaseUrl(properties.bill.get
        .replace("{customerId}", customer.id.toString())
        .replace("{billId}", bill.id));
      var billResponse = await axios.get<BillResponse>(url);
      dispatch(createAction(BillActionType.JOIN_BILLS, Bill.from(billResponse.data)));
    } catch (error) {
      createAction(ExecutionActionType.ADD_ERROR, { error, route: AppRoute.LOCATIONS });
      throw error;
    }
  }
}


export const createBillSplitActionCreator = (bill: Bill) => async (dispatch: Dispatch<AnyAction>) => {
  dispatch(createAction(BillActionType.CREATE_BILL_SPLIT, bill));
}

export const selectBillSplitActionCreator = (billSplit: BillSplit) => async (dispatch: Dispatch<AnyAction>) => {
  dispatch(createAction(BillActionType.SELECT_BILL_SPLIT, billSplit));
}

export const addOrderToSelectedBillSplitActionCreator = (order: Order) => async (dispatch: Dispatch<AnyAction>) => {
  dispatch(createAction(BillActionType.ADD_ORDER_TO_BILL_SPLIT, order));
}

export const removeOrderFromSelectedBillSplitActionCreator = (order: Order) => async (dispatch: Dispatch<AnyAction>) => {
  dispatch(createAction(BillActionType.REMOVE_ORDER_FROM_BILL_SPLIT, order));
}

export const createInvoiceDraftActionCreator = (bill: Bill, orders: Order[], customer: Customer) => async (dispatch: Dispatch<AnyAction>) => {
  try {
    var url = withBaseUrl(properties.invoice.create.replace('{customerId}', customer.id.toString()));

    var request = {
      customerId: customer.id,
      billId: bill.id,
      orderIds: orders.map(o => o.id)
    };

    var response = await axios.post<ResourceIdResponse>(url, request);
    var invoiceId = response.data.id;
    url = withBaseUrl(properties.invoice.get.replace('{customerId}', customer.id.toString()).replace("{invoiceId}", invoiceId));
    var invoiceResponse = await axios.get<InvoiceResponse>(url);
    bill.invoice = new Invoice(invoiceResponse.data);
    dispatch(createAction(BillActionType.CREATE_INVOICE_DRAFT, Object.assign({}, bill)));
    return bill.invoice;
  } catch (error) {
    dispatch(createAction(ExecutionActionType.ADD_ERROR, { error, route: AppRoute.BILL }));
    throw error;
  }
}

export const removeBillActionCreator = (customer: Customer, bill: Bill) => async (dispatch: Dispatch<AnyAction>) => {
  var url = withBaseUrl(properties.bill.delete.replace("{customerId}", customer.id.toString()).replace("{billId}", bill.id.toString()));
  try {
    await axios.delete<ResourceIdResponse>(url);
    dispatch(createAction(BillActionType.DELETE_BILL, bill));
  } catch (error) {
    dispatch(createAction(ExecutionActionType.ADD_ERROR, { error, container: AppRoute.LOCATIONS }));
    throw new OvviumError(error);
  }
}


async function getOrderResponse(customer: Customer, bill: Bill, orderId: string) {
  let orderUrl = withBaseUrl(properties.bill.order.get //
    .replace("{customerId}", customer.id.toString()) //
    .replace("{billId}", bill.id)
    .replace("{orderId}", orderId));
  return await axios.get<OrderResponse>(orderUrl);
}

