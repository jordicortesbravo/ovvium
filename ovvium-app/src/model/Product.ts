import { getLocalization } from '../services/LocalizationService';
import { Allergen, asAllergen } from './enum/Allergen';
import { asProductType, ProductType } from './enum/ProductType';
import { asServiceBuilderLocation, ServiceBuilderLocation } from './enum/ServiceBuilderLocation';
import { Picture } from './Picture';
import { Rating } from './Rating';
import { ProductResponse } from './response/ProductResponse';
import { TotalRating } from './TotalRating';
import { UserRating } from './UserRating';

export class Product {
    id: string;
    name: string;
    description?: string;
    ncomments?: number;
    allergens?: Allergen[];
    coverPicture: Map<string, Picture>;
    pictures: Array<Map<string, Picture>>;
    serviceBuilderLocation: ServiceBuilderLocation;
    price: number;
    type: ProductType;
    category: string;
    rate: number;
    rateAsInt: number;
    
    userRating? : UserRating;
    ratings?: Rating[];
    //Feo que esto esté aquí...pero para salir del paso por ahora
    ratingPage?: number;
    hidden: boolean;
    recommended: boolean;
    
    constructor(product: Product = {} as Product) {
        this.id = product.id;
        this.name = product.name;
        this.description = product.description;
        this.allergens = product.allergens;
        this.ncomments = product.ncomments;
        this.pictures = product.pictures;
        this.serviceBuilderLocation = product.serviceBuilderLocation;
        this.price = product.price;
        this.type = product.type
        this.category = product.category;
        this.rate = product.rate;
        this.rateAsInt = product.rateAsInt;
        this.coverPicture = product.coverPicture;
        this.hidden = product.hidden;
        this.recommended = product.recommended;
    }

    static from(productResponse: ProductResponse): Product {
        var localization = getLocalization(productResponse.localizations);
        var category = getLocalization(productResponse.category);
        return new Product({
            id: productResponse.id,
            name: localization.name,
            description: localization.description,
            allergens: productResponse.allergens.map(allergen => asAllergen(allergen)),
            ncomments: productResponse.ncomments,
            pictures: productResponse.pictures,
            coverPicture: productResponse.coverPicture ? productResponse.coverPicture : productResponse.pictures.length > 0 ? productResponse.pictures[0] : undefined,
            serviceBuilderLocation: asServiceBuilderLocation(productResponse.serviceBuilderLocation),
            price: productResponse.price,
            type: asProductType(productResponse.type),
            category: category,
            rate: productResponse.rate,
            rateAsInt: productResponse.rateAsInt,
            hidden: productResponse.hidden,
            recommended: productResponse.recommended
        } as Product);
    }
}

