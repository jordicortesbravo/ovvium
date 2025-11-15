import { Bill } from '../model/Bill';
import { Order } from '../model/Order';
import { User } from '../model/User';
import { UserBill } from '../model/UserBill';
import { ArrayUtils } from '../util/ArrayUtils';
import { OrderInvoice } from '../model/OrderInvoice';
import { OrderPaymentStatus } from '../model/enum/OrderPaymentStatus';
import { Tip } from '../model/Tip';

export function mapBillByUser(bill: Bill | undefined, me: User): Array<UserBill> {
    var map = new Map<string, UserBill>();
    if (bill && bill.members) {
        for (var i in bill.members) {
            var user = bill.members[i];
            map.set(user.id, new UserBill(user));
        }
        if (bill.orders) {
            for (var j in bill.orders) {
                var order = bill.orders[j];
                var userBill = map.get(order.user.id);
                if (userBill) {
                    userBill.orders.push(order)
                }
            }
        }
    }
    return Array.from(map.values()).sort((b1: UserBill, b2: UserBill) => {
        return b1.user.id == me.id ? -1 : b2.user.id == me.id ? 1 : b1.user.id > b2.user.id ? 1 : -1;
    });
}

export function mapOrdersByProduct(input: Bill | UserBill | undefined | Order[]): Map<string, Array<Order>> {
    var map = new Map<string, Array<Order>>();
    if (input) {
        var inputOrders;
        if (input instanceof Array) {
            inputOrders = input;
        } else {
            inputOrders = input.orders;
        }
        for (var i in inputOrders) {
            var order = inputOrders[i];
            var product = order.product;
            if (!map.has(product.id)) {
                map.set(product.id, new Array<Order>());
            }
            var orders = map.get(product.id);
            if (orders) {
                orders.push(order);
            }
        }
    }
    return map;
}

export function getPaymentPendingUserOrders(bill: Bill | undefined, users: User[] | undefined): Order[] {
    return getUserOrders(bill, users).filter((order: Order) => order.paymentStatus == OrderPaymentStatus.PENDING);
}

export function getUserOrders(bill: Bill | undefined, users: User[] | undefined): Order[] {
    var orders = [] as Order[];
    if (bill && bill.orders && users) {
        bill.orders.forEach((order: Order) => {
            if (ArrayUtils.contains(users, order.user, "id")) {
                orders.push(order);
            }
        });
    }
    return orders;
}

export function getTotalAmount(input: Bill | UserBill | Order[]): number {
    var amount = 0;
    if (input) {
        if (input instanceof Array) {
            var orders = input;
        } else {
            orders = input.orders;
        }
        orders.forEach(order => amount += order.price);
    }
    return amount;
}

export function getPendingAmount(input: Bill | UserBill | Order[]): number {
    var amount = 0;
    if (input) {
        if (input instanceof Array) {
            var orders = input;
        } else {
            orders = input.orders;
        }
        orders.filter(order => order.paymentStatus == OrderPaymentStatus.PENDING)//
            .forEach(order => amount += order.price);
    }
    return amount;
}

export function mapOrdersByProductInInvoice(inputOrders: undefined | OrderInvoice[]): Map<string, Array<OrderInvoice>> {
    var map = new Map<string, Array<OrderInvoice>>();
    if (inputOrders) {
        for (var i in inputOrders) {
            var order = inputOrders[i];
            var product = order.productName;
            if (!map.has(product)) {
                map.set(product, new Array<OrderInvoice>());
            }
            var orders = map.get(product);
            if (orders) {
                orders.push(order);
            }
        }
    }
    return map;
}

/**
 * TODO Move this to server.
 */
export function proposeTips(selectedOrders: Order[], user: User) {
    let tips: Tip[] = [];
    const pendingAmount = getPendingAmount(selectedOrders);
    const numOfOccurrences = countOccurrencesInOrders(
        selectedOrders,
        ["vino", "cerveza", "wine", "beer", "caña", "copas", "damm", "ron", "ginebra", "whisky", "vodka"]
    );
    let addedFactor = numOfOccurrences >= 3 ? numOfOccurrences * 0.05 : 0;
    if (pendingAmount < 10) {
        tips.push(getTip(0.1 + addedFactor, user));
        tips.push(getTip(0.3 + addedFactor, user));
        tips.push(getTip(0.5 + addedFactor, user));
    } else if (pendingAmount < 20) {
        tips.push(getTip(1 + addedFactor, user));
        tips.push(getTip(2 + addedFactor, user));
        tips.push(getTip(3 + addedFactor, user));
    } else {
        tips.push(getTip(pendingAmount * (0.05 + numOfOccurrences * 0.005), user));
        tips.push(getTip(pendingAmount * (0.1 + numOfOccurrences * 0.005), user));
        tips.push(getTip(pendingAmount * (0.2 + numOfOccurrences * 0.005), user));
    }
    return tips;
}

function getTip(amount: number, user: User, round: boolean = true): Tip {
    return new Tip({
        amount: round ? Math.round(amount * 10) / 10 : amount,
        user: user
    });
}

function countOccurrencesInOrders(orders: Order[], containing: string[]) {
    if(!orders) {
        return 0;
    }
    return orders.map(o => o.product)
        .filter(product =>
            containing.some(v => {
                const value = v.toLowerCase();
                return product.name.toLowerCase().includes(value) || product.category.toLowerCase().includes(value);
            })
        ).length;
}
