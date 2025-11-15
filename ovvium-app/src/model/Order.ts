import { asOrderPaymentStatus, OrderPaymentStatus } from './enum/OrderPaymentStatus';
import { ServiceTime } from './enum/ServiceTime';
import { Product } from './Product';
import { OrderResponse } from './response/OrderResponse';
import { User, WAITER_GHOST_USER } from './User';
import { IssueStatus, asIssueStatus } from './enum/IssueStatus';
import { OrderGroupChoice } from './OrderGroupChoice';
import { ProductGroup } from './ProductGroup';

export class Order {
    id?: string;
    user: User;
    product: Product|ProductGroup;
    paymentStatus: OrderPaymentStatus;
    issueStatus: IssueStatus;
    orderTime?: Date;
    serviceTime?: ServiceTime;
    choices?: OrderGroupChoice[];
    price: number;
    notes?: string;

    constructor(order: Order = {} as Order) {
        this.id = order.id;
        this.user = order.user;
        this.product = order.product;
        this.issueStatus = order.issueStatus;
        this.paymentStatus = order.paymentStatus;
        this.serviceTime = order.serviceTime;
        this.price = order.price;
        this.notes = order.notes;
        this.choices = order.choices;
    }

    static from(orderResponse: OrderResponse) : Order {
        //TODO Falta añadir atributos todavía como las choices o el serviceTime, entre otros.
        var order = new Order({
            id: orderResponse.id,
            user: orderResponse.user ? User.from(orderResponse.user) : WAITER_GHOST_USER,
            product: Product.from(orderResponse.product),
            price: orderResponse.price,
            paymentStatus: asOrderPaymentStatus(orderResponse.paymentStatus),
            issueStatus: asIssueStatus(orderResponse.issueStatus)
        } as Order);

        return order;
    }
}