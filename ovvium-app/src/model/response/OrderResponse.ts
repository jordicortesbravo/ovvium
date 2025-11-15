import { ProductResponse } from './ProductResponse';
import { ResourceIdResponse } from './ResourceIdResponse';
import { UserResponse } from './UserResponse';

export class OrderResponse extends ResourceIdResponse {
    user: UserResponse;
    product: ProductResponse;
    price: number;
    paymentStatus: string;
    issueStatus: string;

    constructor(orderResponse: OrderResponse = {} as OrderResponse) {
        super(orderResponse.id);
        this.user = orderResponse.user;
        this.product = orderResponse.product;
        this.issueStatus = orderResponse.issueStatus;
        this.price = orderResponse.price;
        this.paymentStatus = orderResponse.paymentStatus;
    }
}