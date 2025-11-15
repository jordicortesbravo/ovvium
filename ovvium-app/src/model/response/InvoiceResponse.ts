import { LocationResponse } from './LocationResponse';
import { OrderResponse } from './OrderResponse';
import { ResourceIdResponse } from './ResourceIdResponse';
import { UserResponse } from './UserResponse';
import { CustomerResponse } from './CustomerResponse';
import { OrderInvoiceResponse } from './OrderInvoiceResponse';
import { MoneyAmountResponse } from './MoneyAmountResponse';

export class InvoiceResponse extends ResourceIdResponse {
    invoiceNumber: string;
    customer: CustomerResponse;
    user: UserResponse;
    orders: OrderInvoiceResponse[];
    tipAmount: MoneyAmountResponse;
    totalAmount: MoneyAmountResponse;
    creationDate: Date;

	constructor(invoiceResponse: InvoiceResponse) {
		super(invoiceResponse.id);
        this.invoiceNumber = invoiceResponse.invoiceNumber;
        this.customer = invoiceResponse.customer;
        this.user = invoiceResponse.user;
        this.orders = invoiceResponse.orders;
        this.tipAmount = invoiceResponse.tipAmount;
        this.totalAmount = invoiceResponse.totalAmount;
        this.creationDate = new Date(invoiceResponse.creationDate);
    }
}