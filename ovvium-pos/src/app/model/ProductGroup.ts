import { ProductResponse } from './response/ProductResponse';
import { ServiceTime } from './enum/ServiceTime';
import { Product } from './Product';
import { ProductGroupResponse } from './response/ProductGroupResponse';

export class ProductGroup extends Product {
    
    products: Map<ServiceTime, Product[]>;
    timeRangeAvailable: boolean;

    constructor(product: ProductGroup = {} as ProductGroup) {
        super(product);
        this.products = product.products;
        this.timeRangeAvailable = product.timeRangeAvailable;
    }

    static from(productGroupResponse: ProductGroupResponse): ProductGroup {
        var productGroup = Product.from(productGroupResponse) as ProductGroup;
        productGroup.products = new Map<ServiceTime, Product[]>();
        for(var i in Object.keys(productGroupResponse.products)) {
            var key = Object.keys(productGroupResponse.products)[i];
            var value = productGroupResponse.products[key] as ProductResponse[];
            productGroup.products.set(key as ServiceTime, value.map(p => Product.from(p)));
        }
        productGroup.timeRangeAvailable = productGroupResponse.timeRangeAvailable;
        return productGroup;
    }
}

