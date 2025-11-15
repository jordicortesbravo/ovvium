import { Bill } from "app/model/Bill";
import { BillSplit } from "app/model/BillSplit";
import { DomainStatus } from "app/model/enum/DomainStatus";
import { IssueStatus } from "app/model/enum/IssueStatus";
import { PaymentStatus } from "app/model/enum/PaymentStatus";
import { Order } from "app/model/Order";
import { ArrayUtils } from "app/utils/ArrayUtils";
import { OrderGroup } from '../model/enum/OrderGroup';
import { ProductType } from "app/model/enum/ProductType";

export class BillService {

    static hasPendingIssueOrders(bill: Bill | undefined): boolean {
        return bill != undefined && bill.orders && bill.orders.filter(order => order.issueStatus != IssueStatus.ISSUED && order.domainStatus != DomainStatus.DELETED).length > 0;
    }

    static getPendingIssueOrders(bill: Bill | undefined): Array<Order> {
        if (!bill || !bill.orders) {
            return []
        }
        return bill.orders.filter(order => BillService.isPendingIssue(order));
    }

    static getPendingPaymentOrders(bill: Bill | undefined): Array<Order> {
        if (!bill || !bill.orders) {
            return []
        }
        return bill.orders.filter(order => BillService.isPendingPayment(order));
    }

    static getPendingPaymentOrdersNotInBillSplit(bill: Bill, billSplit: BillSplit) {
        var pendingOrders = BillService.getPendingPaymentOrders(bill);
        return pendingOrders.filter(order => {
            var splits = bill.splits;
            if (!splits) {
                return true;
            }
            var orderInOtherSplit = false;
            splits.forEach(split => {
                if (split.id != billSplit.id && ArrayUtils.contains(split.orders, order, "id")) {
                    orderInOtherSplit = true;
                }
            });
            return !orderInOtherSplit;
        });
    }

    static getTotalPrice(bill: Bill | undefined): number {
        var total: number = 0;
        if (bill) {
            bill.orders.filter(o => o.domainStatus != DomainStatus.DELETED).forEach(order => {
                total += order.product.price;
            })
        }
        return total;
    }

    static getPendingPriceWithoutTax(bill: Bill) {
        var total: number = 0;
        BillService.getPendingPaymentOrders(bill).forEach(order => {
            total += order.product.basePrice;
        });
        return total;
    }

    static getTaxPrice(bill: Bill) {
        var total: number = 0;
        BillService.getPendingPaymentOrders(bill).forEach(order => {
            total += order.product.basePrice * order.product.tax;
        });
        return total;
    }

    static getPendingPrice(bill: Bill) {
        var total: number = 0;
        BillService.getPendingPaymentOrders(bill).forEach(order => {
            total += order.product.price;
        });
        return total;
    }

    static isPendingIssue(order: Order) {
        return order.issueStatus != IssueStatus.ISSUED && order.domainStatus != DomainStatus.DELETED;
    }

    static isPendingPayment(order: Order) {
        return order.paymentStatus != PaymentStatus.PAID && order.domainStatus != DomainStatus.DELETED;
    }

    static getOldestPendingOrderTime(bill: Bill | undefined, productType?: ProductType): Date | undefined {
        var orderTime: Date | undefined;
        if (bill) {
            var orders = BillService.getPendingPaymentOrders(bill);
            if (productType) {
                orders = orders.filter(o => o.product.type == productType);
            }
            orders.forEach(o => {
                if (orderTime == undefined || orderTime > o.orderTime) {
                    orderTime = o.orderTime;
                }
            });
            return orderTime;
        }
        return orderTime;
    }

    static listOrderGroups(orders: Array<Order>) {
        var map = new Map<string, OrderGroup>();
        orders.forEach(o => {
            var productId = o.product.id;
            if (map.has(productId)) {
                map.get(productId)!.orders.push(o);
            } else {
                map.set(productId, { product: o.product, orders: [o] } as OrderGroup)
            }
        });
        return Array.from(map.values()).sort((og1, og2) => {
            return og1.product.name >= og2.product.name ? 1 : -1;
        });
    }
}
