import { OrderInvoiceResponse } from "./response/OrderInvoiceResponse";
import { asPaymentStatus, PaymentStatus } from "./enum/PaymentStatus";
import { getLocalization } from "app/services/LocalizationService";


export class OrderInvoice {
    id: string;
    productName: string;
    paymentStatus: PaymentStatus;
    price: number;
    basePrice: number;
    tax: number;
    currency: string;

    constructor(response: OrderInvoiceResponse) {
        this.id = response.id;
        this.productName = getLocalization(response.productName);
        this.paymentStatus = asPaymentStatus(response.paymentStatus);
        this.price = response.price.amount;
        this.basePrice = response.basePrice.amount;
        this.currency = response.basePrice.currency;
        this.tax = response.tax;
    }
}