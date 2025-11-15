import { msg } from "../../services/LocalizationService";

export enum ProductType {
  FOOD = "FOOD",
  DRINK = "DRINK",
  GROUP = "GROUP"
}

export class ProductTypeMapper {
  public static of(label: string) {
    // FIXME Refactor this ASAP... This should be done in a different way
    var foodLabels = ["food", "comida", "menjar", msg("products:type:food").toLowerCase()];
    var drinkLabels = ["drink", "bebida", "beguda", msg("products:type:drink").toLowerCase()];
    //@ts-ignore
    if (foodLabels.includes(label.toLowerCase())) {
      return ProductType.FOOD;
      //@ts-ignore
    } else if(drinkLabels.includes(label.toLowerCase())) {
      return ProductType.DRINK;
    } else {
      return ProductType.GROUP;
    }
  }
}

export function asProductType(value: string) {
  switch(value.toUpperCase()) {
    case ProductType.DRINK:
      return ProductType.DRINK;
    case ProductType.FOOD:
      return ProductType.FOOD;
    case ProductType.GROUP:
      return ProductType.GROUP;
    default:
      throw new Error("ProductType with value " + value + " doesn't exist");
  }
}
