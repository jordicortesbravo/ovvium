import { getLocalization } from '../services/LocalizationService';
import { asProductType, ProductType } from './enum/ProductType';
import { asServiceBuilderLocation, ServiceBuilderLocation } from './enum/ServiceBuilderLocation';
import { ProductResponse } from './response/ProductResponse';

export class Product {
    id: string;
    name: string;
    description?: string;
    recommendationText?: string;
    serviceBuilderLocation: ServiceBuilderLocation;
    price: number;
    basePrice: number;
    tax: number;
    type: ProductType;
    category: string;
    hidden: boolean;
    recommended: boolean;

    constructor(product: Product = {} as Product) {
        this.id = product.id;
        this.name = product.name;
        this.description = product.description;
        this.recommendationText = product.recommendationText;
        this.serviceBuilderLocation = product.serviceBuilderLocation;
        this.basePrice = product.basePrice;
        this.tax = product.tax;
        this.price = product.price;
        this.type = product.type
        this.category = product.category;
        this.hidden = product.hidden;
        this.recommended = product.recommended;
    }

    static from(productResponse: ProductResponse): Product {
        return new Product({
            id: productResponse.id,
            name: getLocalization(productResponse.name),
            description: getLocalization(productResponse.description),
            serviceBuilderLocation: asServiceBuilderLocation(productResponse.serviceBuilderLocation),
            basePrice: productResponse.basePrice,
            tax: productResponse.tax,
            price: productResponse.price,
            type: asProductType(productResponse.type),
            category: getLocalization(productResponse.categoryName),
            hidden: productResponse.hidden,
            recommended: productResponse.recommended
        } as Product);
    }
}

