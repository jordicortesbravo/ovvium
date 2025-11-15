export class CreateBillRequest {
    userId:string;
    customerId: string;
    locationIds: string[];

    constructor(request: CreateBillRequest = {} as CreateBillRequest) {
        this.userId = request.userId;
        this.customerId = request.customerId;
        this.locationIds = request.locationIds;
    }

}