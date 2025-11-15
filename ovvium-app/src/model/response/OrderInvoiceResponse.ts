import { ResourceIdResponse } from "./ResourceIdResponse";
import { MoneyAmountResponse } from "./MoneyAmountResponse";


export class OrderInvoiceResponse extends ResourceIdResponse {

    productName: string;
    paymentStatus: string;
    price: MoneyAmountResponse;
    basePrice: MoneyAmountResponse;
    tax: number;

    constructor(response: OrderInvoiceResponse) {
        super(response.id);
        this.productName = response.productName;
        this.paymentStatus = response.paymentStatus;
        this.price = response.price;
        this.basePrice = response.basePrice;
        this.tax = response.tax;
    }
}