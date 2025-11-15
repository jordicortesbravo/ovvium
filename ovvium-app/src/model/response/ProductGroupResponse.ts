import { ServiceTime } from '../enum/ServiceTime';
import { ProductResponse } from './ProductResponse';

export class ProductGroupResponse extends ProductResponse {
    products: Map<ServiceTime, ProductResponse[]>;
    timeRangeAvailable: boolean;

    constructor(productResponse: ProductGroupResponse = {} as ProductGroupResponse) {
        super(productResponse);
        this.products = productResponse.products;
        this.timeRangeAvailable = productResponse.timeRangeAvailable;
    }
}