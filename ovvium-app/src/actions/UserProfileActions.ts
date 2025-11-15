import axios from "axios";
import { PaymentMethod } from '../model/PaymentMethod';
import { Dispatch, AnyAction } from 'redux';
import { createAction, withApiBaseUrl } from './BaseAction';
import { User } from '../model/User';
import { UserResponse } from '../model/response/UserResponse';
import { properties } from '../../resources/Properties';
import { ExecutionActionType } from './ExecutionActions';
import { AppScreens } from '../ui/navigation/AppScreens';
import { ResourceIdResponse } from '../model/response/ResourceIdResponse';
import { UserCardDataResponse } from '../model/response/UserCardDataResponse';
import { ArrayUtils } from "../util/ArrayUtils";
import { CrashlyticsUtil } from "../util/CrashLyticsUtil";
import analytics from '@react-native-firebase/analytics';
import { AxiosUtils } from "../util/AxiosUtils";

export enum ProfileActionType  {
    SAVE_PAYMENT_METHOD = "SAVE_PAYMENT_METHOD",
    SAVE_AND_SELECT_PAYMENT_METHOD = "SAVE_AND_SELECT_PAYMENT_METHOD",
    DELETE_PAYMENT_METHOD = "DELETE_PAYMENT_METHOD",
    SELECT_PAYMENT_METHOD = "SELECT_PAYMENT_METHOD",
    UPDATE_USER_PROFILE_DATA = "UPDATE_USER_PROFILE_DATA",
    LOAD_USER_PROFILE_DATA = "LOAD_USER_PROFILE_DATA",
    LOAD_PAYMENT_METHODS = "LOAD_PAYMENT_METHODS",
    CHANGE_PASSWORD = "CHANGE_PASSWORD"
}

export const uploadPicture = (user:User, image: any) => async (dispatch: Dispatch<AnyAction>) => { 
    var url =  withApiBaseUrl(properties.picture.create);
    analytics().logEvent("start_upload_user_picture", {userId: user.id});

    AxiosUtils.uploadFile<ResourceIdResponse>(image, url, "picture").then((response) => {
        url =  withApiBaseUrl(properties.user.profile);

        axios.patch(url, {
            pictureId: response.data.id
        }).then((response) => {
            analytics().logEvent("end_upload_user_picture", {
                pictureId: response.data.id
            });
            url =  withApiBaseUrl(properties.user.profile);

            axios.get<any>(url).then((response) => {
                var u = Object.assign({}, user);
                u.imageUri = response.data.imageUri;
                u.name = response.data.name;
                dispatch(createAction(ProfileActionType.UPDATE_USER_PROFILE_DATA, {user: u}));
            })
        })
    });

    analytics().logEvent("end_upload_user_picture", user);
  }
  
export const updateUser = (user:User, allergens?: string[], foodPreferences?: string[]) => async (dispatch: Dispatch<AnyAction>) => { 
    var url =  withApiBaseUrl(properties.user.profile);
    analytics().logEvent("start_update_user", {
        user: user,
        allergens: allergens,
        foodPreferences: foodPreferences
    });
    axios.patch<void>(url, {
        userId: user.id,
        name: user.name,
        allergens: allergens,
        foodPreferences: foodPreferences
    }).then((response) => {
        if(allergens) {
            dispatch(createAction(ProfileActionType.UPDATE_USER_PROFILE_DATA, {user: user, allergens: allergens}));
        } else if(foodPreferences) {
            dispatch(createAction(ProfileActionType.UPDATE_USER_PROFILE_DATA, {user: user, foodPreferences: foodPreferences}));
        } else {
            dispatch(createAction(ProfileActionType.UPDATE_USER_PROFILE_DATA, {user: user}));
        }
        analytics().logEvent("end_update_user", {
            user: user,
            allergens: allergens,
            foodPreferences: foodPreferences
        });
    }).catch((error) => {
        CrashlyticsUtil.recordError("Error in updateUser", error);
    })
}

export const loadUserProfileData = () => async (dispatch: Dispatch<AnyAction>) => { 
    var url =  withApiBaseUrl(properties.user.profile);
    axios.get<void>(url).then((response) => {
        dispatch(createAction(ProfileActionType.LOAD_USER_PROFILE_DATA, {profileData: response.data}));
    });
}

export const loadPaymentMethods = () => async (dispatch: Dispatch<AnyAction>) => { 
    try {
        var url =  withApiBaseUrl(properties.payment.cards);
        var response = await axios.get<UserCardDataResponse[]>(url);
        var paymentMethods = response.data.map(pm => PaymentMethod.from(pm));
        dispatch(createAction(ProfileActionType.LOAD_PAYMENT_METHODS, paymentMethods));
    } catch(error) {
        CrashlyticsUtil.recordError("Error in loadPaymentMethods", error);
        dispatch(createAction(ExecutionActionType.ADD_ERROR, {error, screen: AppScreens.EditPaymentMethod}));
    }
}

export const savePaymentMethodActionCreator = (paymentMethod: PaymentMethod) => async (dispatch: Dispatch<AnyAction>) => {
    try {
        var url =  withApiBaseUrl(properties.payment.cardToken);
        analytics().logEvent("start_save_payment_method", paymentMethod);
        if(paymentMethod.pciTemporalToken) {
            await axios.post<ResourceIdResponse>(url, {token: paymentMethod.pciTemporalToken});
        }
        analytics().logEvent("end_save_payment_method", paymentMethod);
        dispatch(createAction(ProfileActionType.SAVE_PAYMENT_METHOD, paymentMethod));
        url =  withApiBaseUrl(properties.payment.cards);
        var response = await axios.get<UserCardDataResponse[]>(url);
        var paymentMethods = response.data.map(pm => PaymentMethod.from(pm));
        dispatch(createAction(ProfileActionType.LOAD_PAYMENT_METHODS, paymentMethods));
        if(paymentMethods.length == 1) {
            selectPaymentMethodAction(ArrayUtils.first(paymentMethods))(dispatch);
        }
    } catch(error) {
        CrashlyticsUtil.recordError("Error in savePaymentMethod", error);
        dispatch(createAction(ExecutionActionType.ADD_ERROR, {error, screen: AppScreens.EditPaymentMethod}));
    }
}

export const selectPaymentMethodAction = (paymentMethod: PaymentMethod) => (dispatch: Dispatch<AnyAction>) => {
    dispatch(createAction(ProfileActionType.SELECT_PAYMENT_METHOD, paymentMethod));
}

export const deletePaymentMethodActionCreator = (paymentMethod: PaymentMethod) => async (dispatch: Dispatch<AnyAction>) => {
    try {
        var url =  withApiBaseUrl(properties.payment.removeCard).replace("{pciDetailsId}", paymentMethod.id);
        await axios.delete(url);
        analytics().logEvent("delete_payment_method", paymentMethod);
        dispatch(createAction(ProfileActionType.DELETE_PAYMENT_METHOD, paymentMethod));
    } catch(error) {
        CrashlyticsUtil.recordError("Error deleting paymentMethod", error);
    }
}

export const changePasswordAction = (user: User, currentPassword: string, newPassword: string) => async (dispatch: Dispatch<AnyAction>) => {

    var url =  withApiBaseUrl(properties.user.profile);
    var promise = axios.patch<void>(url, {
                userId: user.id,
                password: {
                    oldPassword: currentPassword,
                    newPassword: newPassword
                }
        });
    analytics().logEvent("change_password", {userId: user.id});
    dispatch(createAction(ProfileActionType.CHANGE_PASSWORD, undefined));
    return promise;
}