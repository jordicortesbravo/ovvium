export enum ServiceTime {
    
    SOONER = 'SOONER', 
    STARTER = 'STARTER', 
    FIRST_COURSE = 'FIRST_COURSE', 
    SECOND_COURSE = 'SECOND_COURSE', 
    DESSERT = 'DESSERT',
    OTHER = 'OTHER'
}


export function asServiceTime(value: string |undefined): ServiceTime |undefined {
    if(!value) {
        return undefined;
    }
    switch(value.toUpperCase()) {
        case ServiceTime.SOONER:
            return ServiceTime.SOONER;
        case ServiceTime.STARTER:
            return ServiceTime.STARTER;
        case ServiceTime.FIRST_COURSE:
            return ServiceTime.FIRST_COURSE;
        case ServiceTime.SECOND_COURSE:
            return ServiceTime.SECOND_COURSE;
        case ServiceTime.DESSERT:
            return ServiceTime.DESSERT;
        case ServiceTime.OTHER:
            return ServiceTime.OTHER;
        default:
            throw new Error("BillStatus with value " + value + " doesn't exist");
    }
}

//FIXME Hay que internacionalizarlo!!
export function getServiceTimeLabel(serviceTime: ServiceTime | string) {
    switch(serviceTime) {
        case ServiceTime.SOONER:
            return "Lo antes posible";
        case ServiceTime.STARTER:
            return "Entrantes";
        case ServiceTime.FIRST_COURSE:
            return "Primeros";
        case ServiceTime.SECOND_COURSE:
            return "Segundos";
        case ServiceTime.DESSERT: 
            return "Postres";
        case ServiceTime.OTHER:
        default: 
            return "Otros";
    }
}