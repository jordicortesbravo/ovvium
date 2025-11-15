import { Product } from "./Product";

export class OrderGroupChoice {

    product: Product;
    notes?: string;

    constructor(product: Product, notes?: string) {
        this.product = product;
        this.notes = notes;
    }
}