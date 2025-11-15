import { Bill } from "app/model/Bill";
import { Category } from 'app/model/Category';
import { Customer } from "app/model/Customer";
import { Location } from "app/model/Location";
import { Session } from "app/model/Session";
import { User } from "app/model/User";
import { BillSplit } from "app/model/BillSplit";
import { InvoiceDatePage } from './../model/InvoiceDatePage';
import { InvoiceDate } from "app/model/InvoiceDate";
import { InvoicePage } from "app/model/InvoicePage";

export interface AppState {
  sessionState: SessionState;
  executionState: ExecutionState;
  billState: BillState;
  invoicesState: InvoicesState;
  productState: ProductState;
  locationState: LocationState;
}

export interface SessionState {
  user?: User;
  session?: Session;
  isAuthenticated: boolean;
}

export interface BillState {
  bills: Array<Bill>;
  selectedBill?: Bill;
  selectedSplit?: BillSplit;
  customer?: Customer;
}

export interface ProductState {
  categories: Array<Category>;
  selectedCategory?: Category;
}

export interface InvoicesState {
  invoiceDates?: InvoiceDatePage;
  invoices?: InvoicePage;
  lastInvoiceDate?: InvoiceDate;
  selectedInvoiceDate?: InvoiceDate;
}
export interface LocationState {
  locations: Array<Location>;
}

export interface ExecutionState {
  errors: any[];
  showIndicator: boolean;
  theme: 'classic' | 'dark';
}

export const initialState: AppState = {
  sessionState: {
    isAuthenticated: false,
  },
  productState: {
    categories: [],
    selectedCategory: undefined
  },
  locationState: {
    locations: []
  },
  invoicesState: {
    invoiceDates: undefined,
    invoices: undefined,
    lastInvoiceDate: undefined
  },
  billState: {
    bills: [],
    selectedBill: undefined,
    customer: undefined
  },
  executionState: { 
    showIndicator: false, 
    errors: [],
    theme: 'dark'
  }
};
