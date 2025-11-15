import { InvoiceResponse } from "app/model/response/InvoiceResponse";
import { OrderInvoice } from "./OrderInvoice";

export class Invoice {

    id: string;
    invoiceNumber: string;
    paymentType: string;
    orders: OrderInvoice[];
    tipAmount: number;
    totalAmount: number;
    totalBaseAmount: number;
    currency: string;
    creationDate: Date;

    constructor(response: InvoiceResponse) {
        this.id = response.id;
        this.invoiceNumber = response.invoiceNumber;
        this.paymentType = response.paymentType;
        this.orders = response.orders.map(order => new OrderInvoice(order));
        this.tipAmount = response.tipAmount.amount;
        this.currency = response.totalAmount.currency;
        this.totalAmount = response.totalAmount.amount;
        this.totalBaseAmount = response.totalBaseAmount.amount;
        this.creationDate = new Date(response.creationDate);
    }

}