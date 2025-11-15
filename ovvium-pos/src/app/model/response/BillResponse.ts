import { LocationResponse } from './LocationResponse';
import { OrderResponse } from './OrderResponse';
import { ResourceIdResponse } from './ResourceIdResponse';
import { UserResponse } from './UserResponse';
import { EmployeeResponse } from 'app/model/response/EmployeeResponse';

export class BillResponse extends ResourceIdResponse {
    locations: LocationResponse[];
    status: string;
    members: UserResponse[];
    orders: OrderResponse[];
    hasJoinedLocations: boolean;
    updated: number;
    employee: EmployeeResponse;

	constructor(billResponse: BillResponse = {} as BillResponse) {
		super(billResponse.id);
        this.locations = billResponse.locations;
        this.status = billResponse.status;
        this.members = billResponse.members;
        this.orders = billResponse.orders;
        this.hasJoinedLocations = billResponse.hasJoinedLocations;
        this.updated = billResponse.updated;
        this.employee = billResponse.employee;
    }
}