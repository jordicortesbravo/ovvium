

export class MoneyAmountResponse {

    amount: number;
    currency: string

    constructor(response: MoneyAmountResponse) {
        this.amount = response.amount;
        this.currency = response.currency;
    }
}