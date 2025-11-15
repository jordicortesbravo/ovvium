import { AnyAction, Reducer } from 'redux';
import { initialState } from 'app/store/AppState';
import { InvoicesState } from './../store/AppState';
import { InvoicesActionType } from 'app/actions/InvoiceActions';
import { InvoiceDatePage } from './../model/InvoiceDatePage';
import { InvoicePage } from 'app/model/InvoicePage';

export const invoicesStateReducer: Reducer<InvoicesState> = (
  state: InvoicesState = initialState.invoicesState,
  action: AnyAction
): InvoicesState => {
  switch (action.type) {
    case InvoicesActionType.PAGE_INVOICE_DATES:
    case InvoicesActionType.CREATE_INVOICE_DATE:
    case InvoicesActionType.UPDATE_STATUS_INVOICE_DATE:
      let invoiceDatePage = action.payload.invoiceDatePage as InvoiceDatePage;
      return {
        ...state,
        invoiceDates: invoiceDatePage
      };
    case InvoicesActionType.GET_LAST_INVOICE_DATE:
      return {
        ...state,
        lastInvoiceDate: action.payload.invoiceDate
      }
    case InvoicesActionType.PAGE_INVOICES:
      let invoicePage = action.payload.invoicePage as InvoicePage;
      return {
        ...state,
        invoices: invoicePage
      };
  }
  return state;
};