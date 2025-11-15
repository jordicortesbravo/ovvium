import { LocationResponse } from './response/LocationResponse';

export class Location {
    id: string;
    customerId: string;
    positionId: number;
    description?: string;

    constructor(location: Location = {} as Location) {
        this.id = location.id;
        this.customerId = location.customerId;
        this.positionId = location.positionId;
        this.description = location.description;
    }

    static from(locationResponse: LocationResponse): Location {
        return new Location({
            id: locationResponse.id,
            customerId: locationResponse.customerId,
            positionId: locationResponse.positionId,
            description: locationResponse.description
        });
    }
}