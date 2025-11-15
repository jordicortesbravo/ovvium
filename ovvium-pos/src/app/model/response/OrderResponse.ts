import { ProductResponse } from './ProductResponse';
import { ResourceIdResponse } from './ResourceIdResponse';
import { UserResponse } from './UserResponse';
import { OrderGroupChoiceResponse } from 'app/model/response/OrderGroupChoiceResponse';

export class OrderResponse extends ResourceIdResponse {
    user: UserResponse;
    product: ProductResponse;
    paymentStatus: string;
    issueStatus: string;
    price: number;
    tax: number;
    serviceTime: string;
    orderTime: number;
    notes?: string;
    groupChoices: Array<OrderGroupChoiceResponse>;

    constructor(orderResponse: OrderResponse = {} as OrderResponse) {
        super(orderResponse.id);
        this.user = orderResponse.user;
        this.product = orderResponse.product;
        this.paymentStatus = orderResponse.paymentStatus;
        this.issueStatus = orderResponse.issueStatus;
        this.price = orderResponse.price;
        this.tax = orderResponse.tax;
        this.serviceTime = orderResponse.serviceTime;
        this.orderTime = orderResponse.orderTime;
        this.notes = orderResponse.notes;
        this.groupChoices = orderResponse.groupChoices;
    }
}