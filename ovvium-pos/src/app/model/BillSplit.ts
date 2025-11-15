import { Invoice } from "app/model/Invoice";
import { Order } from "app/model/Order";

export class BillSplit {

    id: number;
    invoice?: Invoice;
    orders: Array<Order>;

    constructor() {
        this.id = Date.now();
        this.orders = [];
    }

}