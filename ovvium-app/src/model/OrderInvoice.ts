import { asOrderPaymentStatus, OrderPaymentStatus } from "./enum/OrderPaymentStatus";
import { OrderInvoiceResponse } from "./response/OrderInvoiceResponse";


export class OrderInvoice {
    id: string;
    productName: string;
    paymentStatus: OrderPaymentStatus;
    price: number;
    basePrice: number;
    tax: number;

    constructor(response: OrderInvoiceResponse) {
        this.id = response.id;
        this.productName = response.productName;
        this.paymentStatus = asOrderPaymentStatus(response.paymentStatus);
        this.price = response.price.amount;
        this.basePrice = response.basePrice.amount;
        this.tax = response.tax;
    }
}