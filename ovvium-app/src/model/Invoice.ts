import { Customer } from "./Customer";
import { OrderInvoice } from "./OrderInvoice";
import { InvoiceResponse } from "./response/InvoiceResponse";
import { Order } from "./Order";


export class Invoice {

    id: string;
    invoiceNumber: string;
    customer?: Customer;
    orders: OrderInvoice[];
    tipAmount: number;
    totalAmount: number;
    creationDate: Date;

    constructor(response: InvoiceResponse) {
        this.id = response.id;
        this.invoiceNumber = response.invoiceNumber;
        this.customer = Customer.from(response.customer);
        this.orders = response.orders.map(order => new OrderInvoice(order));
        this.tipAmount = response.tipAmount.amount;
        this.totalAmount = response.totalAmount.amount;
        this.creationDate = new Date(response.creationDate);
    }
}