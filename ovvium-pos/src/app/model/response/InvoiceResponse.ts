import { Customer } from 'app/model/Customer';
import { MoneyAmountResponse } from './MoneyAmountResponse';
import { OrderInvoiceResponse } from './OrderInvoiceResponse';
import { ResourceIdResponse } from './ResourceIdResponse';
import { UserResponse } from './UserResponse';

export class InvoiceResponse extends ResourceIdResponse {
    invoiceNumber: string;
    customer: Customer;
    user: UserResponse;
    paymentType: string;
    orders: OrderInvoiceResponse[];
    tipAmount: MoneyAmountResponse;
    totalAmount: MoneyAmountResponse;
    totalBaseAmount: MoneyAmountResponse;
    creationDate: Date;

	constructor(invoiceResponse: InvoiceResponse) {
		super(invoiceResponse.id);
        this.invoiceNumber = invoiceResponse.invoiceNumber;
        this.customer = invoiceResponse.customer;
        this.user = invoiceResponse.user;
        this.paymentType = invoiceResponse.paymentType;
        this.orders = invoiceResponse.orders;
        this.tipAmount = invoiceResponse.tipAmount;
        this.totalAmount = invoiceResponse.totalAmount;
        this.totalBaseAmount = invoiceResponse.totalBaseAmount;
        this.creationDate = new Date(invoiceResponse.creationDate);
    }
}