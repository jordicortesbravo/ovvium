export enum OrderPaymentStatus {

    PENDING = "PENDING",
    PAID = "PAID"
}

export function asOrderPaymentStatus(value: string) {
    switch(value.toUpperCase()) {
        case OrderPaymentStatus.PENDING:
            return OrderPaymentStatus.PENDING;
        case OrderPaymentStatus.PAID:
            return OrderPaymentStatus.PAID;
        default:
            throw new Error("OrderPaymentStatus with value " + value + " doesn't exist");
    }
}