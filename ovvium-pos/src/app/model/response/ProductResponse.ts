import { ResourceIdResponse } from './ResourceIdResponse';
import { MultiLangStringResponse } from './MultiLangStringResponse';

export class ProductResponse extends ResourceIdResponse {
    customerId: string;
    name: MultiLangStringResponse;
    description?: MultiLangStringResponse;
    basePrice: number;
    tax: number;
    price: number;
    type: string;
    serviceBuilderLocation: string;
    categoryName: MultiLangStringResponse;
    rate: number;
    rateAsInt: number;
    hidden: boolean;
    recommended: boolean;

    constructor(productResponse: ProductResponse = {} as ProductResponse) {
        super(productResponse.id);
        this.customerId = productResponse.customerId;
        this.name = productResponse.name;
        this.description = productResponse.description;
        this.tax = productResponse.tax;
        this.basePrice = productResponse.basePrice;
        this.price = productResponse.price;
        this.type = productResponse.type;
        this.serviceBuilderLocation = productResponse.serviceBuilderLocation;
        this.categoryName = productResponse.categoryName;
        this.rate = productResponse.rate;
        this.rateAsInt = productResponse.rateAsInt;
        this.hidden = productResponse.hidden;
        this.recommended = productResponse.recommended;
    }
}