import { PaymentMethodType } from './enum/PaymentMethodType';
import { asServiceTime, ServiceTime } from './enum/ServiceTime';
import { Product } from './Product';
import { OrderResponse } from './response/OrderResponse';
import { User } from './User';
import { PaymentStatus, asPaymentStatus } from './enum/PaymentStatus';
import { IssueStatus, asIssueStatus } from './enum/IssueStatus';
import { DomainStatus } from 'app/model/enum/DomainStatus';
import { OrderGroupChoice } from 'app/model/OrderGroupChoice';

export class Order {
    id?: string;
    user?: User;
    product: Product;
    paymentStatus: PaymentStatus;
    issueStatus: IssueStatus;
    domainStatus?: DomainStatus;
    orderTime: Date;
    serviceTime?: ServiceTime;
    paymentMethodType?: PaymentMethodType;
    price: number;
    notes?: string;
    groupChoices?: Array<OrderGroupChoice>;

    constructor(order: Order = {} as Order) {
        this.id = order.id;
        this.user = order.user;
        this.product = order.product;
        this.paymentStatus = order.paymentStatus;
        this.issueStatus = order.issueStatus;
        this.orderTime = order.orderTime;
        this.serviceTime = order.serviceTime;
        this.paymentMethodType = order.paymentMethodType;
        this.price = order.price;
        this.notes = order.notes;
        this.groupChoices = order.groupChoices;
    }

    getOrderTime() {
        return this.orderTime ? Date.parse(this.orderTime.toString()) : undefined;
    }

    static from(orderResponse: OrderResponse) : Order {
        return new Order({
            id: orderResponse.id,
            user: User.from(orderResponse.user),
            product: Product.from(orderResponse.product),
            price: orderResponse.price,
            paymentStatus: asPaymentStatus(orderResponse.paymentStatus),
            issueStatus: asIssueStatus(orderResponse.issueStatus),
            serviceTime: asServiceTime(orderResponse.serviceTime),
            orderTime: new Date(orderResponse.orderTime),
            notes: orderResponse.notes,
            groupChoices: orderResponse.groupChoices.map(gc => OrderGroupChoice.from(gc))
        } as Order);
    }
}