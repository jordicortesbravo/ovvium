import { ResourceIdResponse } from "app/model/response/ResourceIdResponse";
import { ProductResponse } from "app/model/response/ProductResponse";

export class OrderGroupChoiceResponse extends ResourceIdResponse {

    product: ProductResponse;
    issueStatus: string;
    serviceTime: string;
    orderTime: number;
    notes: string;

    constructor(response: OrderGroupChoiceResponse) {
        super(response.id);
        this.product = response.product;
        this.issueStatus = response.issueStatus;
        this.serviceTime = response.serviceTime;
        this.orderTime = response.orderTime;
        this.notes = response.notes;
    }    

}
