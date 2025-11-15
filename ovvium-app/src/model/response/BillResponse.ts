import { LocationResponse } from './LocationResponse';
import { OrderResponse } from './OrderResponse';
import { ResourceIdResponse } from './ResourceIdResponse';
import { UserResponse } from './UserResponse';
import { CustomerResponse } from './CustomerResponse';

export class BillResponse extends ResourceIdResponse {
    customer: CustomerResponse;
    locations: LocationResponse[];
    status: string;
    members: UserResponse[];
    orders: OrderResponse[];
    hasJoinedLocations: boolean;
    updated: number;

	constructor(billResponse: BillResponse = {} as BillResponse) {
        super(billResponse.id);
        this.customer = billResponse.customer;
        this.locations = billResponse.locations;
        this.status = billResponse.status;
        this.members = billResponse.members;
        this.orders = billResponse.orders;
        this.hasJoinedLocations = billResponse.hasJoinedLocations;
        this.updated = billResponse.updated;
    }
}