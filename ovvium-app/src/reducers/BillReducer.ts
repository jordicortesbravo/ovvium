import { AnyAction, Reducer } from "redux";
import { BillActionType } from '../actions/BillActions';
import { Bill } from '../model/Bill';
import { User } from '../model/User';
import { getPaymentPendingUserOrders } from '../services/BillService';
import { BillState, initialState } from "../store/State";
import { ArrayUtils } from '../util/ArrayUtils';
import { CaseReducer, reduce } from './ReducerHelper';
import { PaymentStatus } from "../model/enum/PaymentStatus";
import { InvoicePage } from "../model/response/InvoicePage";
import { ProfileActionType } from "../actions/UserProfileActions";
import { UserBill } from "../model/UserBill";

export const billStateReducer: Reducer<BillState> = (state: BillState = initialState.billState, action: AnyAction): BillState => {
  return reduce(
    state, 
    action,
    [createOrJoinBill(),
    setCustomer(),
    filterBillDetail(),
    toggleUserToPay(),
    refreshBill(),
    clearBillFromState(),
    addTip(),
    pay(),
    getInvoices(),
    updateUserProfile()
    ]);
};

function createOrJoinBill(): CaseReducer<BillState> {
  return caseReducer(BillActionType.CREATE_OR_JOIN_BILL, (state: BillState, action:AnyAction) => {
    return {...state, bill: action.payload, lastInvoice: undefined};
  });
}

function setCustomer(): CaseReducer<BillState> {
  return caseReducer(BillActionType.SET_CUSTOMER, (state: BillState, action:AnyAction) => {
    return {...state, customer: action.payload};
  });
}

function filterBillDetail(): CaseReducer<BillState> {
  return caseReducer(BillActionType.FILTER_BILL_DETAIL, (state: BillState, action:AnyAction) => {
    if(action.payload.members) {
      return {
        ...state, 
        userBill: new Bill(action.payload), 
        usersToPay: Object.assign([], action.payload.members), 
        ordersToPay: getPaymentPendingUserOrders(state.bill, action.payload.members)
      };
    } else {
      return {
        ...state, 
        userBill: action.payload, 
        usersToPay: [action.payload.user],
        ordersToPay: getPaymentPendingUserOrders(state.bill, [action.payload.user])
      };
    }
  });
}

function toggleUserToPay(): CaseReducer<BillState> {
  return caseReducer(BillActionType.TOGGLE_USER_TO_PAY, (state: BillState, action:AnyAction) => {
    var users = [action.payload];
    var usersToPay = state.usersToPay;
    filterUsersToPay(users, usersToPay);
    return {...state, usersToPay: Object.assign([], usersToPay), ordersToPay: getPaymentPendingUserOrders(state.bill, usersToPay)}
  });
}

function refreshBill(): CaseReducer<BillState> {
  return caseReducer(BillActionType.REFRESH_BILL, (state: BillState, action:AnyAction) => {
    if(action.payload.bill == undefined) {
      return {...state, bill: undefined, userBill:undefined, ordersToPay: undefined, lastInvoice: undefined, customer: undefined};
    }
    var ordersToPay = getPaymentPendingUserOrders(action.payload.bill, state.usersToPay);
    return {...state, bill: action.payload.bill, userBill: action.payload.userBill, ordersToPay, customer: action.payload.bill.customer, lastInvoice: undefined}
  });
}

function clearBillFromState(): CaseReducer<BillState> {
  return caseReducer(BillActionType.CLEAR_BILL_FROM_STATE, (state: BillState, action:AnyAction) => {
    return {...state, bill: action.payload};
  });
}

function addTip(): CaseReducer<BillState> {
  return caseReducer(BillActionType.ADD_TIP, (state: BillState, action:AnyAction) => {
    var bill = action.payload.bill;
      var tip = action.payload.tip;
      if(bill) {
        if(!bill.tips) {
          bill.tips = [];
        }
        ArrayUtils.remove(bill.tips, tip, "user.id");
        bill.tips.push(tip);
      }
      return {...state, bill: new Bill(bill), tip: tip, lastPaymentStatus: undefined}
  });
}

function pay(): CaseReducer<BillState> {
  return caseReducer(BillActionType.PAY, (state: BillState, action:AnyAction) => {
      var paymentStatus = action.payload.paymentStatus;
      var bill = action.payload.bill;
      if(paymentStatus == PaymentStatus.KO) {
        return {...state};  
      }
      return {...state, lastInvoice: action.payload.lastInvoice, bill}
  });
}

function getInvoices(): CaseReducer<BillState> {
  return caseReducer(BillActionType.GET_INVOICES, (state: BillState, action:AnyAction) => {
      var invoicePage: InvoicePage = action.payload;
      if(invoicePage.pageOffset == 0 || !state.invoicePage || state.invoicePage.content.length == 0) {
        return {...state, invoicePage};
      }
      var invoiceContent = state.invoicePage.content.slice();
      invoicePage.content.forEach(invoice => invoiceContent.push(invoice));
      invoicePage.content = invoiceContent;
      return {...state, invoicePage};
  });
}

function updateUserProfile(): CaseReducer<BillState> {
  return caseReducer(ProfileActionType.UPDATE_USER_PROFILE_DATA, (state: BillState, action:AnyAction) => {
    if(state.userBill && state.userBill instanceof UserBill && state.userBill.user.id == action.payload.user.id) {
      state.userBill.user = action.payload.user;
      return { ...state, userBill: Object.assign({}, state.userBill) };
    }
    return {...state}
  });
}

function caseReducer(actionType: string, delegate: (state: BillState, action: AnyAction) => BillState) : CaseReducer<BillState> {
  return new CaseReducer<BillState>(actionType, delegate);
}


function filterUsersToPay(users: User[], usersToPay: User[]|undefined) {
  users.forEach((user: User) => {
    if(!usersToPay) {
      usersToPay = new Array<User>();
    }
    if(!ArrayUtils.contains(usersToPay, user, "id")) {
      usersToPay.push(user);
    } else {
      ArrayUtils.remove(usersToPay, user, "id");
    }
  });
}

