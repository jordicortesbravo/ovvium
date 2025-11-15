import { ResourceIdResponse } from './ResourceIdResponse';


export class LocationResponse extends ResourceIdResponse {
    customerId: string;
    positionId: number;
    description: string;

    constructor(locationResponse: LocationResponse = {} as LocationResponse) {
        super(locationResponse.id);
        this.customerId = locationResponse.customerId;
        this.positionId = locationResponse.positionId;
        this.description = locationResponse.description;
    }
}