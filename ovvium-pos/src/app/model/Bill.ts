import { asBillStatus, BillStatus } from './enum/BillStatus';
import { Location } from './Location';
import { Order } from './Order';
import { BillResponse } from './response/BillResponse';
import { Tip } from './Tip';
import { User } from './User';
import { Invoice } from 'app/model/Invoice';
import { BillSplit } from 'app/model/BillSplit';
import { Employee } from 'app/model/Employee';

export class Bill {
    //TODO Si se agregan nuevos atributos a la bill, hay que retocar el método clone para incroporar el copiado de los datos!
    id: string;
    locations: Array<Location>;
    billStatus: BillStatus;
    members: Array<User>;
    orders: Array<Order>;
    hasJoinedLocations: boolean;
    tips: Array<Tip>;
    updated: Date;
    invoice?: Invoice;
    splits?: BillSplit[];
    employee?: Employee;

    constructor(bill: Bill = {} as Bill) {
        this.id = bill.id;
        this.locations = bill.locations;
        this.billStatus = bill.billStatus;
        this.members = bill.members;
        this.orders = bill.orders;
        this.hasJoinedLocations = bill.hasJoinedLocations;
        this.tips = bill.tips;
        this.updated = bill.updated; 
        this.invoice = bill.invoice;
        this.splits = bill.splits;
        this.employee = bill.employee;
    }

    static from(billResponse: BillResponse): Bill {
        var locations = billResponse.locations.map((locationResponse) => Location.from(locationResponse));
        var billStatus = asBillStatus(billResponse.status);
        var members = billResponse.members.map((userResponse) => User.from(userResponse));
        var orders = billResponse.orders.map((orderResponse) => Order.from(orderResponse))
        var employee = Employee.from(billResponse.employee);
        return new Bill({
            id: billResponse.id,
            locations: locations,
            billStatus: billStatus,
            members: members,
            hasJoinedLocations: billResponse.hasJoinedLocations,
            orders: orders,
            updated: new Date(billResponse.updated),
            employee
        } as Bill);
    }
}