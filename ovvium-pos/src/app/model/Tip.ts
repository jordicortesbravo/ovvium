import { User } from './User';

export class Tip {
    amount: number;
    label?: string;
    percentage?:number;
    user: User;

    constructor(tip: Tip) {
        var amount = Math.round(tip.amount * 100)/100;
        this.amount = amount;
        this.percentage = this.percentage;
        this.user = tip.user;
        if(!tip.label) {
            this.label = tip.percentage + "% (" +  amount.toFixed(2) + "€" + ")";
        } else {
            this.label = tip.label;
        }
    }

    public toString() : string {
        return this.amount.toFixed(2)+'€'
    }
}