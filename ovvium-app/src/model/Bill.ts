import { asBillStatus, BillStatus } from './enum/BillStatus';
import { Location } from './Location';
import { Order } from './Order';
import { BillResponse } from './response/BillResponse';
import { Tip } from './Tip';
import { User, WAITER_GHOST_USER } from './User';
import { ArrayUtils } from '../util/ArrayUtils';
import { Customer } from './Customer';

export class Bill {
    id: string;
    customer: Customer;
    locations: Array<Location>;
    billStatus: BillStatus;
    members: Array<User>;
    orders: Array<Order>;
    hasJoinedLocations: boolean;
    tips: Array<Tip>;
    updated?: Date;

    constructor(bill: Bill = {} as Bill) {
        this.id = bill.id;
        this.customer = bill.customer;
        this.locations = bill.locations;
        this.billStatus = bill.billStatus;
        this.members = bill.members;
        this.orders = bill.orders;
        this.hasJoinedLocations = bill.hasJoinedLocations;
        this.tips = bill.tips;
        this.updated = bill.updated; 
    }

    static from(billResponse: BillResponse): Bill {
        var customer = Customer.from(billResponse.customer);
        var locations = billResponse.locations.map((locationResponse) => Location.from(locationResponse));
        var billStatus = asBillStatus(billResponse.status);
        var members = billResponse.members.map((userResponse) => User.from(userResponse));
        var orders = billResponse.orders.map((orderResponse) => Order.from(orderResponse));
        orders.forEach(order => {
            if(order.user == WAITER_GHOST_USER && !ArrayUtils.contains(members, WAITER_GHOST_USER, 'id')) {
                members.push(WAITER_GHOST_USER);
            }
        })
        return new Bill({
            id: billResponse.id,
            customer: customer,
            locations: locations,
            billStatus: billStatus,
            members: members,
            hasJoinedLocations: billResponse.hasJoinedLocations,
            orders: orders,
            updated: new Date(billResponse.updated)
        } as Bill);
    }
}