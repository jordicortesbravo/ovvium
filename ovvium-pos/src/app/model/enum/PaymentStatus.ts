export enum PaymentStatus {

    PENDING = "PENDING",
    PAID = "PAID"
}

export function asPaymentStatus(value: string) {
    switch(value.toUpperCase()) {
        case PaymentStatus.PENDING:
            return PaymentStatus.PENDING;
        case PaymentStatus.PAID:
            return PaymentStatus.PAID;
        default:
            throw new Error("PaymentStatus with value " + value + " doesn't exist");
    }
}