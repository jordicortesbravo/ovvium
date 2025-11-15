import { properties } from 'app/config/Properties';
import { AppRoute } from 'app/containers/Router/AppRoute';
import { Customer } from 'app/model/Customer';
import { ProductType } from 'app/model/enum/ProductType';
import { OvviumError } from 'app/model/OvviumError';
import { Product } from 'app/model/Product';
import { ProductGroup } from 'app/model/ProductGroup';
import { ProductGroupResponse } from 'app/model/response/ProductGroupResponse';
import axios from 'axios';
import { AnyAction, Dispatch } from 'redux';
import { Category } from './../model/Category';
import { CategoryResponse } from './../model/response/CategoryResponse';
import { ProductResponse } from './../model/response/ProductResponse';
import { createAction, withBaseUrl } from './BaseAction';
import { ExecutionActionType } from './ExecutionActions';

export enum ProductActionType {
  LOAD_PRODUCTS = 'LOAD_PRODUCTS',
  GET_PRODUCT = "GET_PRODUCT",
  SELECT_CATEGORY = 'SELECT_CATEGORY'
}

export const loadProductsCreator = (customer: Customer) => async (dispatch: Dispatch<AnyAction>) => {
  dispatch(createAction(ExecutionActionType.SHOW_INDICATOR, undefined));
  var categories = await getCategories(customer, dispatch);
  var products = await getProducts(customer, dispatch);
  dispatch(createAction(ProductActionType.LOAD_PRODUCTS, {
    categories: categories,
    products: products
  }));
  dispatch(createAction(ExecutionActionType.HIDE_INDICATOR, undefined));
};

export const selectCategoryCreator = (category: Category) => async (dispatch: Dispatch<AnyAction>) => {
  dispatch(createAction(ProductActionType.SELECT_CATEGORY, { category }));
}

async function getProducts(customer: Customer, dispatch: Dispatch<AnyAction>) {
  try {
    var productsUrl = withBaseUrl(properties.products.list.replace('{customerId}', customer.id));
    var productsResponse = await axios.get<ProductResponse[]>(productsUrl)
    var products = productsResponse.data.map(pr => Product.from(pr));
    var promises = products
      .filter(p => !p.hidden)
      .map(async p => p.type == ProductType.GROUP ? await loadFullProduct(customer, p.id) : p);
    return Promise.all(promises)
  } catch (error) {
    dispatch(createAction(ExecutionActionType.ADD_ERROR, { error, route: AppRoute.TAKE_ORDER }));
    throw new OvviumError(error);
  }
}

export const getProductCreator = (customer: Customer, productId: string) => async (dispatch: Dispatch<AnyAction>) => {
  try {
    let product = await loadFullProduct(customer, productId);
    return dispatch(createAction(ProductActionType.GET_PRODUCT, { product: product }));
  } catch (error) {
    dispatch(createAction(ExecutionActionType.ADD_ERROR, { error, route: AppRoute.TAKE_ORDER }));
    throw new OvviumError(error);
  }
};

async function getCategories(customer: Customer, dispatch: Dispatch<AnyAction>) {
  try {
    var categoriesUrl = withBaseUrl(properties.categories.list.replace('{customerId}', customer.id));
    var categoriesResponse = await axios.get<CategoryResponse[]>(categoriesUrl);
    return categoriesResponse.data.map(c => Category.from(c));
  } catch (error) {
    dispatch(createAction(ExecutionActionType.ADD_ERROR, { error, route: AppRoute.TAKE_ORDER }));
    throw new OvviumError(error);
  }
}

export async function loadFullProduct(customer: Customer, productId: string) {
  var url = withBaseUrl(properties.products.get).replace("{customerId}", customer.id).replace("{productId}", productId);
  var data = (await axios.get<ProductResponse | ProductGroupResponse>(url)).data
  return data.type == ProductType.GROUP ? ProductGroup.from(data as ProductGroupResponse) : Product.from(data as ProductResponse);
}
