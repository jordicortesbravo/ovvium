import { Bill } from "app/model/Bill";


export class LocationService {

    static getLocationName(bill: Bill | undefined) : string {
        if(!bill || ! bill.locations || bill.locations.length == 0) {
            return 'Selecciona una mesa';
        }
        var name = '';
        bill.locations.forEach(l => {
            name += l.description + "+";
        })
        return name.slice(0, name.length-1);
    }
}