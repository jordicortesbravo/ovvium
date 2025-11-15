export enum ProductType {
  FOOD = "FOOD",
  DRINK = "DRINK",
  GROUP = "GROUP"
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
