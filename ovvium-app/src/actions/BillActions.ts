import analytics from '@react-native-firebase/analytics';
import axios from 'axios';
import { Dispatch } from 'react';
import { AnyAction } from 'redux';
import { properties } from '../../resources/Properties';
import { Bill } from '../model/Bill';
import { Customer } from '../model/Customer';
import { BillStatus } from '../model/enum/BillStatus';
import { PaymentStatus } from '../model/enum/PaymentStatus';
import { Invoice } from '../model/Invoice';
import { Location } from '../model/Location';
import { Order } from '../model/Order';
import { PaymentMethod } from '../model/PaymentMethod';
import { CreateBillRequest } from '../model/request/CreateBillRequest';
import { CreateOrderRequest } from '../model/request/CreateOrderRequest';
import { BillResponse } from '../model/response/BillResponse';
import { InvoicePage } from '../model/response/InvoicePage';
import { InvoiceResponse } from '../model/response/InvoiceResponse';
import { ResourceIdResponse } from '../model/response/ResourceIdResponse';
import { Tip } from '../model/Tip';
import { User } from '../model/User';
import { UserBill } from '../model/UserBill';
import { getPaymentPendingUserOrders, mapBillByUser } from '../services/BillService';
import { AppScreens } from '../ui/navigation/AppScreens';
import { ArrayUtils } from '../util/ArrayUtils';
import { CrashlyticsUtil } from '../util/CrashLyticsUtil';
import { createAction, withApiBaseUrl } from './BaseAction';
import { CartActionType } from './CartActions';
import { ExecutionActionType } from './ExecutionActions';


export enum BillActionType  {
    CREATE_OR_JOIN_BILL = "CREATE_OR_JOIN_BILL",
    SET_CUSTOMER = "SET_CUSTOMER",
    CREATE_ORDERS = "CREATE_ORDERS",
    REFRESH_ORDERS_TO_PAY = "REFRESH_ORDERS_TO_PAY",
    FILTER_BILL_DETAIL = "FILTER_BILL_DETAIL",
    TOGGLE_USER_TO_PAY = "TOGGLE_USER_TO_PAY",
    REFRESH_BILL = "REFRESH_BILL",
    CLEAR_BILL_FROM_STATE = "CLEAR_BILL_FROM_STATE",
    ADD_TIP = "ADD_TIP",
    PAY = "PAY",
    PAY_IN_COURSE = "PAY_IN_COURSE",
    GET_INVOICES = "GET_INVOICES"
}

export const createOrJoinBillActionCreator = (tagId: string, user: User) => async (dispatch: Dispatch<AnyAction>) => {
    try {
        
        var url =  withApiBaseUrl(properties.tags.get).replace("{tagId}", tagId);
        var tagResponse = await axios.get(url);
        var customer = Customer.from(tagResponse.data.customer);
        var location = Location.from(tagResponse.data.location);
        if(customer) {
            setCustomerActionCreator(customer)(dispatch);
            url =  withApiBaseUrl(properties.bill.create);
            var request = new CreateBillRequest({
                customerId: customer.id, 
                userId: user.id, 
                locationIds: [location.id]
            });
            analytics().logEvent("start_create_or_join_bill", request);
            var response = await axios.post<ResourceIdResponse>(url, request);
            analytics().logEvent("end_create_or_join_bill", request);
            url = withApiBaseUrl(properties.bill.get);
            var billResponse = await axios.get<BillResponse>(url);
            var bill = Bill.from(billResponse.data);
            dispatch(createAction(BillActionType.CREATE_OR_JOIN_BILL, bill));
        }

    } catch(error) {
        if(error.code == 2002) {
            refreshBillActionCreator(user)(dispatch);
        } else {
            dispatch(createAction(ExecutionActionType.ADD_ERROR, {error, screen: AppScreens.JoinBill}));
            CrashlyticsUtil.recordError("Error in createOrJoinBillActionCreator", error);
        }
    }
}

export const refreshBillActionCreator = (user: User) => async (dispatch: Dispatch<AnyAction>) => {
    var url =  withApiBaseUrl(properties.bill.get);
    try {
        var billResponse = await axios.get<BillResponse>(url);
        var refreshedBill: Bill = Bill.from(billResponse.data);

        var payload = {
            bill: refreshedBill,
            userBill: ArrayUtils.first(mapBillByUser(refreshedBill, user))
        }
        var payed = refreshedBill.billStatus == BillStatus.CLOSED && getPaymentPendingUserOrders(refreshedBill, [user]).length == 0;
        if(payed) {
            dispatch(createAction(BillActionType.REFRESH_BILL, {bill: undefined}));
        } else {
            dispatch(createAction(BillActionType.REFRESH_BILL, payload));
        }
        return refreshedBill;
    } catch(error) {
        if(error.code == 404) {
            dispatch(createAction(BillActionType.REFRESH_BILL, {bill: undefined, userBill: undefined}));
        } else {
            CrashlyticsUtil.recordError("Error in refreshBillActionCreator", error);
        }
    }
}

export const createOrders = (bill: Bill, user: User, orders: Order[]) => async (dispatch: Dispatch<AnyAction>) => {
    var url = withApiBaseUrl(properties.bill.orders.create);
    try {
        var response = await axios.post<ResourceIdResponse>(url, orders.map(o => CreateOrderRequest.from(o)));
        dispatch(createAction(CartActionType.CLEAR_CART, undefined));
        refreshBillActionCreator(user)(dispatch);
    } catch(error) {
        CrashlyticsUtil.recordError("Error in createOrders", error);
        dispatch(createAction(ExecutionActionType.ADD_ERROR, {error, screen: AppScreens.BillStep1}));
    }
}

export const setCustomerActionCreator = (customer: Customer) => (dispatch: Dispatch<AnyAction>) => {
    dispatch(createAction(BillActionType.SET_CUSTOMER, customer));
}

export const filterBillDetailActionCreator = (userBill: UserBill | Bill) => (dispatch: Dispatch<AnyAction>) => {
    dispatch(createAction(BillActionType.FILTER_BILL_DETAIL, userBill));
}

export const toggleUserToPayActionCreator = (user: User) => (dispatch: Dispatch<AnyAction>) => {
    dispatch(createAction(BillActionType.TOGGLE_USER_TO_PAY, user));
}

export const clearBillFromStateActionCreator = () => (dispatch: Dispatch<AnyAction>) => {
    dispatch(createAction(BillActionType.CLEAR_BILL_FROM_STATE, undefined));
}

export const addTipToBillActionCreator = (bill: Bill, tip: Tip) => async (dispatch: Dispatch<AnyAction>) => {
    dispatch(createAction(BillActionType.ADD_TIP, {bill, tip}));
}

export const payActionCreator = (orders: Order[], tip: Tip, paymentMethod: PaymentMethod, user: User, customer: Customer, bill: Bill) => async (dispatch: Dispatch<AnyAction>) => {
    dispatch(createAction(ExecutionActionType.SHOW_INDICATOR, undefined));
    dispatch(createAction(BillActionType.PAY_IN_COURSE, undefined));
    var url =  withApiBaseUrl(properties.payment.pay);
    try {
        var request = {
            pciDetailsId: paymentMethod.id,
            orderIds: orders.map((order:Order) => order.id as string),
            tipAmount: tip ? tip.amount : undefined
        };
        url =  withApiBaseUrl(properties.payment.pay);
        analytics().logEvent("start_payment", request);
        var response = await axios.post<ResourceIdResponse>(url, request);
        analytics().logEvent("end_payment", request);
        var invoiceId = response.data.id;
        url =  withApiBaseUrl(properties.invoice.get).replace("{invoiceId}", invoiceId);
        var invoiceResponse = await axios.get<InvoiceResponse>(url);
        var invoice = new Invoice(invoiceResponse.data);
        var refreshedBill = await refreshBillActionCreator(user)(dispatch);
        dispatch(createAction(BillActionType.PAY, {paymentStatus: PaymentStatus.OK, lastInvoice: invoice, bill: refreshedBill}));
        dispatch(createAction(CartActionType.CLEAR_CART, undefined));
        dispatch(createAction(ExecutionActionType.HIDE_INDICATOR, undefined));
        return true;
    } catch(error) {
        CrashlyticsUtil.recordError("Error in payActionCreator", error);
        dispatch(createAction(BillActionType.PAY, {paymentStatus: PaymentStatus.KO}));
        dispatch(createAction(ExecutionActionType.HIDE_INDICATOR, undefined));
        dispatch(createAction(ExecutionActionType.ADD_ERROR, {error, screen: AppScreens.BillStep5}));
        return false;
    }
}

export const getInvoicesActionCreator = (page: number) => async (dispatch: Dispatch<AnyAction>) => {
    dispatch(createAction(ExecutionActionType.SHOW_INDICATOR, undefined));

    var url =  withApiBaseUrl(properties.invoice.page).replace("{page}", "" + page);
    var response = await axios.get<InvoicePage>(url);
    var invoicePage = response.data;

    dispatch(createAction(BillActionType.GET_INVOICES, invoicePage));
}
