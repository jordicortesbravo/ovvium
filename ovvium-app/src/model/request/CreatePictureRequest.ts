export class CreatePictureRequest {

    customerId: string;
    productId: string;
    image: string;

    constructor(request: CreatePictureRequest) {
        this.customerId = request.customerId;
        this.productId = request.productId;
        this.image = request.image;
    }
}