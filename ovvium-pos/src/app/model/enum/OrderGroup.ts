import { Product } from 'app/model/Product';
import { Order } from 'app/model/Order';

export interface OrderGroup{
    product: Product;
    orders: Array<Order>;
}