

export class ProductLocalizationResponse {

    name: string;
    description: string;

    constructor(productLocalizationResponse: ProductLocalizationResponse) {
        this.name = productLocalizationResponse.name;
        this.description = productLocalizationResponse.description;
    }

}