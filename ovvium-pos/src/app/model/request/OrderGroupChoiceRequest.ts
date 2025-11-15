export class OrderGroupChoiceRequest {

	productId: string;
	notes?: string;

	constructor(productId: string, notes?: string) {
        this.productId = productId;
        this.notes = notes;
    }
}