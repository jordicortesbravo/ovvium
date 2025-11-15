import axios from 'axios';
import { AnyAction, Dispatch } from 'redux';
import { Customer } from '../model/Customer';
import { Product } from '../model/Product';
import { Rating } from '../model/Rating';
import { ProductResponse } from '../model/response/ProductResponse';
import { RatingResponse } from '../model/response/RatingResponse';
import { RatingsPageResponse } from '../model/response/RatingsPageResponse';
import { ResourceIdResponse } from '../model/response/ResourceIdResponse';
import { TotalRating } from '../model/TotalRating';
import { User } from '../model/User';
import { UserRating } from '../model/UserRating';
import { AppScreens } from '../ui/navigation/AppScreens';
import { properties } from './../../resources/Properties';
import { createAction, withApiBaseUrl } from './BaseAction';
import { ExecutionActionType } from './ExecutionActions';
import { CrashlyticsUtil } from '../util/CrashLyticsUtil';
import analytics from '@react-native-firebase/analytics';
import { ProductType } from '../model/enum/ProductType';
import { ProductGroup } from '../model/ProductGroup';
import { ProductGroupResponse } from '../model/response/ProductGroupResponse';
import { AxiosUtils } from '../util/AxiosUtils';

export enum ProductActionType  {
    LIST_PRODUCTS = "LIST_PRODUCTS",
    SELECT_PRODUCT = "SELECT_PRODUCT",
    LOAD_FULL_PRODUCT = "LOAD_FULL_PRODUCT",
    GET_TOTAL_RATINGS = "GET_TOTAL_RATINGS",
    GET_USER_RATING = "GET_USER_RATING",
    PAGE_RATINGS = "PAGE_RATINGS",
    UPLOAD_PHOTO = "UPLOAD_PHOTO"
}

export const listProductsActionCreator = (customer: Customer) => (dispatch: Dispatch<AnyAction>) => {
    var url =  withApiBaseUrl(properties.products.list.replace("{customerId}", customer.id));
    return axios.get<ProductResponse[]>(url)//
        .then(response => {
            var products = response.data.map(pr => Product.from(pr));
            products.filter(p => p.type == ProductType.GROUP).forEach(p => loadFullProduct(customer, p)(dispatch));
            dispatch(createAction(ProductActionType.LIST_PRODUCTS, products));
      }).catch(error => {
        dispatch(createAction(ExecutionActionType.ADD_ERROR, {error, screen: AppScreens.Products}));
        CrashlyticsUtil.recordError("Error in listProductsActionCreator", error);
      });
}

export const loadFullProduct = (customer: Customer, product: Product)  => (dispatch: Dispatch<AnyAction>) => {
    var url = withApiBaseUrl(properties.products.get).replace("{customerId}", customer.id).replace("{productId}", product.id);
    return axios.get<ProductResponse|ProductGroupResponse>(url)
        .then(response => {
            var p = response.data.type == ProductType.GROUP ? ProductGroup.from(response.data as ProductGroupResponse) : Product.from(response.data as ProductResponse);
            dispatch(createAction(ProductActionType.LOAD_FULL_PRODUCT, p));
            return p;
        }).catch(err =>{
            CrashlyticsUtil.recordError("Error in loadFullProduct", err);
            return err;
        });
}

export const loadTotalRatingsCreator = (product: Product) => (dispatch: Dispatch<AnyAction>) => {
    var url =  withApiBaseUrl(properties.ratings.totals);

    return axios.get<TotalRating>(url, {
        params: {
            "productId" : product.id
        }
    }).then(response => {
        dispatch(createAction(ProductActionType.GET_TOTAL_RATINGS, response.data));
        return response.data;
    }).catch(err => {
        CrashlyticsUtil.recordError("Error in loadTotalRatingsCreator", err);
        return err;
    });
      
}

export const loadUserRatingsCreator = (product: Product, user: User) => async (dispatch: Dispatch<AnyAction>) => {
    var url =  withApiBaseUrl(properties.ratings.ratings);
    
    return axios.get<RatingResponse[]>(url, {
        params: {
            "productId" : product.id,
            "userId" : user.id
        }
    }).then(response => {
        dispatch(createAction(ProductActionType.GET_USER_RATING, response.data));
    }).catch(err => {
        // Rating doesn't exists yet
        if(err.status === 404) {
            return;
        }
        CrashlyticsUtil.recordError("Error in loadUserRatingsCreator", err);
        return err;
    });
    
}

export const pageRatingsCreator = (product: Product, page: number) => async (dispatch: Dispatch<AnyAction>) => {
    var url =  withApiBaseUrl(properties.ratings.page);

    return axios.get<RatingsPageResponse>(url, {
        params: {
            "productId" : product.id,
            "page" : page
        }
    }).then(response => {
        return response.data;
    }).catch(err => {
        return err;
    });
}

export const sendProductUserRateCreator = (product: Product, user: User, userRating: UserRating) => async (dispatch: Dispatch<AnyAction>) => {
    if(userRating.id !== undefined) {
        var url =  withApiBaseUrl(properties.ratings.rating.replace("{id}", userRating.id));
        var request = {
            "rating" : userRating.rating,
            "comment" : userRating.comment
        };
        await axios.patch<void>(url, request)
        analytics().logEvent("send_product_user_rate", request);
        return {
            id: userRating.id,
            rating: userRating.rating,
            comment: userRating.comment,
            userName: user.name,
            updated: new Date(),
        } as RatingResponse
    } else {
        url =  withApiBaseUrl(properties.ratings.ratings);
        var request2 = {
            "productId" : product.id,
            "userId" : user.id,
            "rating" : userRating.rating,
            "comment" : userRating.comment
        };
        var response = await axios.post<ResourceIdResponse>(url, request2);
        analytics().logEvent("send_product_user_rate", request2);
        return {
            id: response.data.id,
            rating: userRating.rating,
            comment: userRating.comment,
            userName: user.name,
            updated: new Date(),
        } as RatingResponse
    }
}

export const uploadPictureCreator = (customer: Customer, product: Product, image: any) => async (dispatch: Dispatch<AnyAction>) => {
    var url =  withApiBaseUrl(properties.picture.create);
    analytics().logEvent("start_upload_picture", {
        customerId: customer.id,
        productId: product.id
    });

    var uploadFileResponse = await AxiosUtils.uploadFile<ResourceIdResponse>(image, url, "picture");

    url =  withApiBaseUrl(properties.products.addPicture)
                .replace("{customerId}", customer.id.toString())
                .replace("{productId}", product.id.toString());

    await axios.post(url, {
        pictureId: uploadFileResponse.data.id
    });
    analytics().logEvent("end_upload_picture", {
        customerId: customer.id,
        productId: product.id
    });
    url =  withApiBaseUrl(properties.products.get)
            .replace("{customerId}", customer.id.toString())
            .replace("{productId}", product.id.toString());

    var productResponse = await axios.get<ProductResponse>(url);
    dispatch(createAction(ProductActionType.UPLOAD_PHOTO, {product: Product.from(productResponse.data)}));
}

export const selectProductActionCreator = (selectedProduct: Product) => (dispatch: Dispatch<AnyAction>) => {
    dispatch(createAction(ProductActionType.SELECT_PRODUCT, selectedProduct));
}
