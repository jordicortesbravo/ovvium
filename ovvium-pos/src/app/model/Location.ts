import { LocationResponse } from './response/LocationResponse';

export class Location {
    id: string;
    zone: string;
    customerId: string;
    positionId: number;
    
    description: string;

    constructor(location: Location) {
        this.id = location.id;
        this.zone = location.zone;
        this.customerId = location.customerId;
        this.positionId = location.positionId;
        this.description = location.description;
    }

    static from(locationResponse: LocationResponse): Location {
        return {
            id: locationResponse.id,
            zone: locationResponse.zone,
            customerId: locationResponse.customerId,
            positionId: locationResponse.positionId,
            description: locationResponse.description,
        } as Location;
    }
}