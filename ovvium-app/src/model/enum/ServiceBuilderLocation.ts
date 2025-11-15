export enum ServiceBuilderLocation {

    BAR = 'BAR', 
    KITCHEN = 'KITCHEN'
}

export function asServiceBuilderLocation(value: string) {
    switch(value.toUpperCase()) {
      case ServiceBuilderLocation.BAR:
        return ServiceBuilderLocation.BAR;
      case ServiceBuilderLocation.KITCHEN:
        return ServiceBuilderLocation.KITCHEN;
      default:
        throw new Error("ServiceBuilderLocation with value " + value + " doesn't exist");
    }
  }