import { AnyAction, Dispatch } from 'redux';
import { Order } from '../model/Order';
import { Product } from '../model/Product';
import { User } from '../model/User';
import { createAction } from './BaseAction';
import analytics from '@react-native-firebase/analytics';

export enum CartActionType {
    ADD_TO_CART = "ADD_TO_CART",
    REMOVE_FROM_CART = "REMOVE_FROM_CART",
    CLEAR_CART = "CLEAR_CART"
}

export const clearCartActionCreator = () => async (dispatch: Dispatch<AnyAction>) => { 
    return dispatch(createAction(CartActionType.CLEAR_CART, undefined));
}

export const addToCartActionCreator = (product: Product, user: User) => async (dispatch: Dispatch<AnyAction>) => { 
    var order = {
        user,
        product,
        price: product.price
    } as Order;
    analytics().logEvent("add_to_cart", order);
    return dispatch(createAction(CartActionType.ADD_TO_CART, order));
}

export const addOrderToCartActionCreator = (order: Order) => async (dispatch: Dispatch<AnyAction>) => { 
    analytics().logEvent("add_order_to_cart", order);
    return dispatch(createAction(CartActionType.ADD_TO_CART, order));
}

export const removeFromCartActionCreator = (product: Product) => async (dispatch: Dispatch<AnyAction>) => { 
    analytics().logEvent("remove_from_cart", product);
    return dispatch(createAction(CartActionType.REMOVE_FROM_CART, product));
}
