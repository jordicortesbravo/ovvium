import { properties } from 'app/config/Properties';
import { AppRoute } from 'app/containers/Router/AppRoute';
import { Customer } from 'app/model/Customer';
import axios from 'axios';
import { AnyAction, Dispatch } from 'redux';
import { createAction, withBaseUrl } from './BaseAction';
import { ExecutionActionType } from './ExecutionActions';
import { InvoiceDatePageResponse } from './../model/response/InvoiceDatePageResponse';
import { InvoiceDatePage } from './../model/InvoiceDatePage';
import { InvoiceDate } from 'app/model/InvoiceDate';
import { OvviumError } from 'app/model/OvviumError';
import { ResourceIdResponse } from 'app/model/response/ResourceIdResponse';
import { InvoiceDateResponse } from './../model/response/InvoiceDateResponse';
import { InvoicePageResponse } from './../model/response/InvoicePageResponse copy';
import { Utils } from 'app/utils/Utils';
import { InvoicePage } from 'app/model/InvoicePage';

export enum InvoicesActionType {
  PAGE_INVOICE_DATES = 'PAGE_INVOICE_DATES',
  CREATE_INVOICE_DATE = 'CREATE_INVOICE_DATE',
  UPDATE_STATUS_INVOICE_DATE = 'UPDATE_STATUS_INVOICE_DATE',
  GET_LAST_INVOICE_DATE = 'GET_LAST_INVOICE_DATE',
  PAGE_INVOICES = 'PAGE_INVOICES',
}

export const pageInvoiceDatesCreator = (customer: Customer, date: Date) => async (dispatch: Dispatch<AnyAction>) => {
  dispatch(createAction(ExecutionActionType.SHOW_INDICATOR, undefined));
  let invoiceDatePage = await getInvoiceDatePage(customer, date, dispatch);
  dispatch(
    createAction(InvoicesActionType.PAGE_INVOICE_DATES, {
      invoiceDatePage: invoiceDatePage
    })
  );
  dispatch(createAction(ExecutionActionType.HIDE_INDICATOR, undefined));
};

export const createInvoiceDateCreator = (customer: Customer, date: Date) => async (dispatch: Dispatch<AnyAction>) => {
  let url = withBaseUrl(properties.invoiceDate.create
    .replace("{customerId}", customer.id.toString()));

  try {
    var request = {
      date: Utils.formatDate(date, "YYYY-MM-DD")
    };

    await axios.post<ResourceIdResponse>(url, request);

    let invoiceDatePage = await getInvoiceDatePage(customer, date, dispatch);
    dispatch(createAction(InvoicesActionType.CREATE_INVOICE_DATE, { invoiceDatePage: invoiceDatePage }));
  } catch (error) {
    throw new OvviumError(error);
  }
}

export const changeStatusInvoiceDateCreator = (customer: Customer, invoiceDate: InvoiceDate, status: "CLOSED" | "OPEN") => async (dispatch: Dispatch<AnyAction>) => {
  var url = withBaseUrl(properties.invoiceDate.update
    .replace("{customerId}", customer.id.toString())
    .replace("{invoiceDateId}", invoiceDate.id));
  try {
    var request = {
      status: status
    };
    await axios.patch(url, request);

    let invoiceDatePage = await getInvoiceDatePage(customer, invoiceDate.date, dispatch);
    dispatch(createAction(InvoicesActionType.UPDATE_STATUS_INVOICE_DATE, { invoiceDatePage: invoiceDatePage }));

  } catch (err) {
    const error = new OvviumError(err);
    if (error.localizedMessage) {
      dispatch(createAction(ExecutionActionType.ADD_ERROR, { error, container: AppRoute.CONFIG }));
    } else {
      dispatch(createAction(ExecutionActionType.ADD_ERROR, { error, container: AppRoute.CONFIG }));
      throw new OvviumError(error);
    }
  }
}

export const getLastInvoiceDateCreator = (customer: Customer) => async (dispatch: Dispatch<AnyAction>) => {
  dispatch(createAction(ExecutionActionType.SHOW_INDICATOR, undefined));

  let invoiceDate = await getLastInvoiceDate(customer, dispatch);

  dispatch(
    createAction(InvoicesActionType.GET_LAST_INVOICE_DATE, { invoiceDate: invoiceDate })
  );
  dispatch(createAction(ExecutionActionType.HIDE_INDICATOR, undefined));
};

export const pageInvoicesCreator = (customer: Customer, page: number, invoiceDate: InvoiceDate | undefined) => async (dispatch: Dispatch<AnyAction>) => {
  if (invoiceDate == undefined) {
    dispatch(createAction(InvoicesActionType.PAGE_INVOICES, { invoicePage: undefined }));
  } else {
    dispatch(createAction(ExecutionActionType.SHOW_INDICATOR, undefined));
    let invoicesPage = await axios
      .get<InvoicePageResponse>(withBaseUrl(
        properties.invoice.page.replace('{customerId}', customer.id.toString())
      ), {
        params: {
          "page": page,
          "size": 10,
          "invoiceDate": Utils.formatDate(invoiceDate.date, "YYYY-MM-DD")
        }
      }) //
      .then((response) => {
        return new InvoicePage(response.data);
      })
      .catch((error) => {
        dispatch(createAction(ExecutionActionType.ADD_ERROR, { error, route: AppRoute.CONFIG }));
        throw new OvviumError(error);
      });
    dispatch(
      createAction(InvoicesActionType.PAGE_INVOICES, {
        invoicePage: invoicesPage
      })
    );
    dispatch(createAction(ExecutionActionType.HIDE_INDICATOR, undefined));
  }
};

async function getInvoiceDatePage(customer: Customer, date: Date, dispatch: Dispatch<AnyAction>) {
  return await axios
    .get<InvoiceDatePageResponse>(withBaseUrl(
      properties.invoiceDate.page.replace('{customerId}', customer.id.toString())
    ), {
      params: {
        "month": date.getMonth() + 1,
        "year": date.getFullYear()
      }
    }) //
    .then((response) => {
      return new InvoiceDatePage(response.data);
    })
    .catch((error) => {
      dispatch(createAction(ExecutionActionType.ADD_ERROR, { error, route: AppRoute.CONFIG }));
      throw new OvviumError(error);
    });
}


async function getLastInvoiceDate(customer: Customer, dispatch: Dispatch<AnyAction>) {
  let invoiceDate: InvoiceDate | undefined;
  await axios
    .get<InvoiceDateResponse>(withBaseUrl(
      properties.invoiceDate.last.replace('{customerId}', customer.id.toString())
    )) //
    .then((response) => {
      invoiceDate = new InvoiceDate(response.data);
    })
    .catch((error) => {
      // if 404, then invoice date is null
      if (error.status !== 404) {
        dispatch(createAction(ExecutionActionType.ADD_ERROR, { error, route: AppRoute.CONFIG }));
        throw new OvviumError(error);
      }
    });
  return invoiceDate;
}

