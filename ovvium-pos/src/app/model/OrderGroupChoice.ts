import { IssueStatus, asIssueStatus } from "app/model/enum/IssueStatus";
import { ServiceTime, asServiceTime } from "app/model/enum/ServiceTime";
import { Product } from "app/model/Product";
import { OrderGroupChoiceResponse } from "app/model/response/OrderGroupChoiceResponse";

export class OrderGroupChoice {
    
    id: string;
    product: Product;
    issueStatus: IssueStatus;
    serviceTime: ServiceTime;
    orderTime: Date;
    notes?: string;

    constructor(orderGroupChoice: OrderGroupChoice) {
        this.id = orderGroupChoice.id;
        this.product = orderGroupChoice.product;
        this.issueStatus = orderGroupChoice.issueStatus;
        this.serviceTime = orderGroupChoice.serviceTime;
        this.orderTime = orderGroupChoice.orderTime;
        this.notes = orderGroupChoice.notes;
    }    

    static from(orderGroupChoiceResponse: OrderGroupChoiceResponse) : OrderGroupChoice {
        return new OrderGroupChoice({
            id: orderGroupChoiceResponse.id,
            product: Product.from(orderGroupChoiceResponse.product),
            issueStatus: asIssueStatus(orderGroupChoiceResponse.issueStatus),
            serviceTime: asServiceTime(orderGroupChoiceResponse.serviceTime),
            orderTime: new Date(orderGroupChoiceResponse.orderTime),
            notes: orderGroupChoiceResponse.notes,
        } as OrderGroupChoice);
    }
}
