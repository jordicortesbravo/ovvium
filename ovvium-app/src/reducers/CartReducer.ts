import { AnyAction, Reducer } from "redux";
import { CartState, initialState } from "../store/State";
import { CartActionType } from "../actions/CartActions";
import { ArrayUtils } from "../util/ArrayUtils";
import { Order } from "../model/Order";
import { Product } from "../model/Product";
import { BillActionType } from "../actions/BillActions";
import { BillStatus } from "../model/enum/BillStatus";

export const cartStateReducer: Reducer<CartState> = (state: CartState = initialState.cartState, action: AnyAction): CartState => {
    switch(action.type) {
        case CartActionType.ADD_TO_CART: {
            var orders = state.orders;
            orders.push(action.payload);
            return {...state, orders: orders.slice()};
        }
        case CartActionType.REMOVE_FROM_CART:{
            orders = state.orders;
            ArrayUtils.remove(orders, action.payload, (order: Order, product: Product) => {
                return order.product.id == product.id;
            });
            return {...state, orders: orders.slice()};
        }
        case CartActionType.CLEAR_CART:{
            return {...state, orders: []};
        }
        case BillActionType.REFRESH_BILL: {
            if(action.payload.bill == undefined || action.payload.bill.billStatus == BillStatus.CLOSED) {
                return {...state, orders: []};
            }
        }
    }
    return state;
};
  
  