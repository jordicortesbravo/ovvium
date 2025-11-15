import { BillActionType } from 'app/actions/BillActions';
import { Bill } from 'app/model/Bill';
import { BillSplit } from 'app/model/BillSplit';
import { BillService } from 'app/services/BillService';
import { BillState, initialState } from 'app/store/AppState';
import { ArrayUtils } from 'app/utils/ArrayUtils';
import { AnyAction, Reducer } from 'redux';
import { PaymentStatus } from 'app/model/enum/PaymentStatus';

export const billStateReducer: Reducer<BillState> = (state: BillState = initialState.billState, action: AnyAction): BillState => {

  switch (action.type) {
    case BillActionType.CREATE_OR_JOIN_BILL:
    case BillActionType.JOIN_BILLS:
    case BillActionType.REFRESH_BILL:
    case BillActionType.CREATE_INVOICE_DRAFT:
      return {
        ...state,
        selectedBill: action.payload,
        bills: updateBillInStateList(state, action.payload)
      }
    case BillActionType.SELECT_BILL:
      return {
        ...state,
        selectedBill: action.payload,
        selectedSplit: undefined
      }
    case BillActionType.SET_CUSTOMER:
      return {
        ...state,
        customer: action.payload

      }
    case BillActionType.LOAD_BILLS:
      var currentBills = fillInvoiceAndBillSplits(state.bills, action.payload);
      var bill = !state.selectedBill || !state.selectedBill.id || ArrayUtils.get(action.payload, state.selectedBill, 'id') == undefined ? undefined : Object.assign({}, ArrayUtils.get(action.payload, state.selectedBill, 'id'))
      return {
        ...state,
        bills: currentBills,
        selectedBill: bill
      }
    case BillActionType.CREATE_ORDER:
      if (state.selectedBill) {
        var selectedBill: Bill | undefined = Object.assign({}, state.selectedBill);
        selectedBill.updated = new Date();
        selectedBill.orders.push(action.payload);
        return {
          ...state,
          bills: updateBillInStateList(state, selectedBill),
          selectedBill,
          selectedSplit: undefined
        }
      }
    case BillActionType.REMOVE_ORDER:
      var order = action.payload;
      selectedBill = new Bill(state.selectedBill);
      selectedBill.updated = new Date();
      ArrayUtils.remove(selectedBill.orders, order, 'id');
      if (selectedBill.splits) {
        selectedBill.splits.forEach(split => ArrayUtils.remove(split.orders, order, "id"));
      }
      return {
        ...state,
        bills: updateBillInStateList(state, selectedBill),
        selectedBill,
        selectedSplit: undefined
      }
    case BillActionType.CHARGE:
      if (state.selectedBill) {
        selectedBill = Object.assign({}, state.selectedBill);
        selectedBill.updated = new Date();
        action.payload.orders.forEach(order => {
          selectedBill!.orders.filter(o => o.id == order.id).forEach(o => o.paymentStatus = PaymentStatus.PAID)
        });
        var bills = Object.assign([], state.bills);
        var ordersPendingToPay = BillService.getPendingPaymentOrders(selectedBill).length;
        if (ordersPendingToPay == 0) {
          ArrayUtils.remove(bills, selectedBill, "id");
          selectedBill = undefined;
        } else {
          ArrayUtils.replace(bills, selectedBill, "id");
        }
        return {
          ...state,
          bills,
          selectedBill
        }
      }
    case BillActionType.UPDATE_ORDERS:
      if (state.selectedBill) {
        selectedBill = Object.assign({}, state.selectedBill);
        selectedBill.updated = new Date();
        action.payload.forEach(order => {
          ArrayUtils.replace(selectedBill!.orders, order, "id");
        });
        return {
          ...state,
          bills: updateBillInStateList(state, selectedBill),
          selectedBill
        }
      }
    case BillActionType.CREATE_BILL_SPLIT:
      bill = Object.assign({}, action.payload);
      if (!bill.splits) {
        bill.splits = [new BillSplit()];
      } else {
        bill.splits.push(new BillSplit());
      }
      return {
        ...state,
        bills: updateBillInStateList(state, bill),
        selectedBill: bill
      };
    case BillActionType.SELECT_BILL_SPLIT:
      return { ...state, selectedSplit: action.payload };
    case BillActionType.ADD_ORDER_TO_BILL_SPLIT:
      var selectedSplit = Object.assign({}, state.selectedSplit);
      selectedSplit.orders.push(action.payload);
      selectedBill = Object.assign({}, state.selectedBill);
      selectedBill.splits = ArrayUtils.replace(selectedBill.splits!, selectedSplit, "id");
      return {
        ...state,
        bills: updateBillInStateList(state, selectedBill),
        selectedBill: selectedBill,
        selectedSplit: selectedSplit
      }
    case BillActionType.REMOVE_ORDER_FROM_BILL_SPLIT:
      selectedSplit = Object.assign({}, state.selectedSplit);
      ArrayUtils.remove(selectedSplit.orders, action.payload, "id");
      selectedBill = Object.assign({}, state.selectedBill);
      selectedBill.splits = ArrayUtils.replace(selectedBill.splits!, selectedSplit, "id");
      return {
        ...state,
        bills: updateBillInStateList(state, selectedBill),
        selectedBill: selectedBill,
        selectedSplit: selectedSplit
      }
    case BillActionType.DELETE_BILL:
      const removedBill = action.payload as Bill;
      ArrayUtils.remove(state.bills, removedBill, "id");
      var bills = Object.assign([], state.bills);
      var selectedBill: Bill | undefined;
      if (state.selectedBill) {
        selectedBill = state.selectedBill.id === removedBill.id ? undefined : state.selectedBill;
      }
      return {
        ...state,
        bills: bills,
        selectedBill: selectedBill
      }
  }
  return state;
};


function fillInvoiceAndBillSplits(oldBills: Bill[], currentBills: Bill[]) {
  currentBills.forEach(currentBill => {
    oldBills.forEach(oldBill => {
      if (oldBill.id == currentBill.id) {
        currentBill.splits = oldBill.splits;
        currentBill.invoice = oldBill.invoice;
        return;
      }
    });
  })
  return currentBills;
}

function updateBillInStateList(state: BillState, refreshedBill: Bill) {
  var bills = Object.assign([], state.bills);
  ArrayUtils.replace(bills, refreshedBill, "id");
  return bills;
}