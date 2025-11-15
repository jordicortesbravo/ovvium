import { AnyAction, Reducer } from 'redux';
import { initialState, ProductState } from 'app/store/AppState';
import { ProductActionType } from 'app/actions/ProductActions';
import { Product } from 'app/model/Product';
import { Category } from 'app/model/Category';

export const productStateReducer: Reducer<ProductState> = (
  state: ProductState = initialState.productState,
  action: AnyAction
): ProductState => {
  switch (action.type) {
    case ProductActionType.LOAD_PRODUCTS:
      var categories = action.payload.categories as Array<Category>;
      var products = action.payload.products as Array<Product>;
      for (var key in categories) {
        const category = categories[key];
        category.products =  products.filter((product) => product.category == category.name)
      }
      return {
        ...state,
        categories: categories
      };
    case ProductActionType.SELECT_CATEGORY:
      return {
        ...state,
        selectedCategory: action.payload.category
      }
  }
  return state;
};