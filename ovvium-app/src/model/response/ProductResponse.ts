import { ResourceIdResponse } from './ResourceIdResponse';
import { PictureResponse } from './PictureResponse';
import { ProductLocalizationResponse } from './ProductLocalizationResponse';

export class ProductResponse extends ResourceIdResponse {
    customerId: string;
    localizations: Map<string, ProductLocalizationResponse>;
    price: number;
    type: string;
    allergens: string[];
    ncomments: number;
    serviceBuilderLocation: string;
    category: Map<string, string>;
    rate: number;
    rateAsInt: number;
    coverPicture: Map<string, PictureResponse>;
    pictures: Array<Map<string, PictureResponse>>;
    hidden: boolean;
    recommended: boolean;

    constructor(productResponse: ProductResponse = {} as ProductResponse) {
        super(productResponse.id);
        this.customerId = productResponse.customerId;
        this.localizations = productResponse.localizations;
        this.allergens = productResponse.allergens;
        this.ncomments = productResponse.ncomments;
        this.price = productResponse.price;
        this.type = productResponse.type;
        this.serviceBuilderLocation = productResponse.serviceBuilderLocation;
        this.category = productResponse.category;
        this.rate = productResponse.rate;
        this.rateAsInt = productResponse.rateAsInt;
        this.pictures = productResponse.pictures;
        this.hidden = productResponse.hidden;
        this.recommended = productResponse.recommended;
        
        if(productResponse.coverPicture || productResponse.pictures.length == 0) {
            this.coverPicture = productResponse.coverPicture;
        } else {
            this.coverPicture = this.pictures[0];
        }
    }
}