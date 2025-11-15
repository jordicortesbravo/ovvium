import { Order } from './Order';
import { User } from './User';

export class UserBill {
    orders: Order[];
    user: User;

    constructor(user: User) {
        this.orders = new Array<Order>();
        this.user = user;
    }
    
}