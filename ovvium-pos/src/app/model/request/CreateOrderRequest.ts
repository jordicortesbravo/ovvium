import { Order } from "../Order";
import { ServiceTime } from "../enum/ServiceTime";
import { OrderGroupChoiceRequest } from "./OrderGroupChoiceRequest";

export class CreateOrderRequest {

    userId?: string;
    productId: string;
    notes?: string;
    groupChoices?: OrderGroupChoiceRequest[];
    serviceTime?: ServiceTime;
    
    constructor(orderRequest: CreateOrderRequest = {} as CreateOrderRequest) {
        this.userId = orderRequest.userId;
        this.productId = orderRequest.productId;
        this.notes = orderRequest.notes;
        this.serviceTime = orderRequest.serviceTime;
        this.groupChoices = orderRequest.groupChoices;
    }

    static from (order: Order) {
        return {
            userId: order.user?.id,
            productId: order.product.id,
            notes: order.notes,
            serviceTime: order.serviceTime,
            groupChoices: order.groupChoices ? order.groupChoices.map(c => new OrderGroupChoiceRequest(c.product.id, c.notes)) : undefined
        } as CreateOrderRequest
    }
}