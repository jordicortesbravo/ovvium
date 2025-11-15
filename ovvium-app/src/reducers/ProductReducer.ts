import { AnyAction, Reducer } from "redux";
import { ProductActionType } from "../actions/ProductActions";
import { initialState, ProductsState } from "../store/State";
import { Product } from '../model/Product';
import { Rating } from '../model/Rating';
import { ProductGroup } from "../model/ProductGroup";
import { ArrayUtils } from "../util/ArrayUtils";

export const productsStateReducer: Reducer<ProductsState> = (state: ProductsState = initialState.productsState, action: AnyAction): ProductsState => {
  let selected: Product;
  switch (action.type) {
    case ProductActionType.LIST_PRODUCTS:
      return {...state, products: action.payload};
    case ProductActionType.SELECT_PRODUCT:
      var selectedProduct = action.payload == null ? null : Object.assign({}, action.payload);
      return {...state, selectedProduct};
    case ProductActionType.LOAD_FULL_PRODUCT:
      selectedProduct = action.payload == null ? null : Object.assign({}, action.payload);
      return {...state, selectedProduct: selectedProduct, products: updateProductInStateList(state, selectedProduct)};
    case ProductActionType.GET_USER_RATING: 
      selected = Object.assign({}, state.selectedProduct!);
      selected.userRating = action.payload;
      return {...state, selectedProduct: selected};
    case ProductActionType.PAGE_RATINGS:
      selected = Object.assign({} as Product, state.selectedProduct!);
      var ratings = action.payload.ratings;
      if(ratings.length > 0) {
        if(!selected.ratings) {
          selected.ratings = [] as Rating[];
        }
        ratings.forEach((rating: Rating) => {
          if(selected.ratings) {
            selected.ratings.push(rating)
          }
        });
        selected.ratingPage = action.payload.page;
      }
      return {...state, selectedProduct: selected};
    case ProductActionType.UPLOAD_PHOTO:
        var product = action.payload.product;
        if(state.selectedProduct) {
          product.ratings = state.selectedProduct.ratings;
          product.ratingPage = state.selectedProduct.ratingPage;
          product.userRating = state.selectedProduct.userRating;
        }
        return {...state, selectedProduct: product};
    default:
      return state;
    }
};

function updateProductInStateList(state: ProductsState, refreshedProduct: Product|ProductGroup) {
  var products = Object.assign([], state.products) as Product[];
  products = ArrayUtils.replace(products, refreshedProduct, "id");
  return products;
}

