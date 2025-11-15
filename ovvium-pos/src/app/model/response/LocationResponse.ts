
export class LocationResponse {
    id: string;
    customerId: string;
    positionId: number;
    zone: string;
    description: string;

    constructor(locationResponse: LocationResponse = {} as LocationResponse) {
        this.id = locationResponse.id;
        this.customerId = locationResponse.customerId;
        this.positionId = locationResponse.positionId;
        this.zone = locationResponse.zone;
        this.description = locationResponse.description;
    }
}