import { AnyAction, Reducer } from 'redux';
import { ProfileActionType } from '../actions/UserProfileActions';
import { initialState, ProfileState } from '../store/State';
import { ArrayUtils } from '../util/ArrayUtils';
import { PaymentMethod } from '../model/PaymentMethod';

export const profileStateReducer: Reducer<ProfileState> = (state: ProfileState = initialState.profileState, action: AnyAction): ProfileState => {
    switch(action.type) {
        case ProfileActionType.LOAD_PAYMENT_METHODS:
            return loadPaymentMethods(state, action);
        case ProfileActionType.SAVE_PAYMENT_METHOD:
            return savePaymentMethod(state, action);
        case ProfileActionType.DELETE_PAYMENT_METHOD:
            return deletePaymentMethod(state, action);
        case ProfileActionType.SELECT_PAYMENT_METHOD:
                return {
                    ...state,
                    selectedPaymentMethod: action.payload
                }
        case ProfileActionType.LOAD_USER_PROFILE_DATA:
            return {...state, allergens: action.payload.profileData.allergens, foodPreferences: action.payload.profileData.foodPreferences}
        case ProfileActionType.UPDATE_USER_PROFILE_DATA: 
            if(action.payload.allergens) {
                return {...state, allergens: action.payload.allergens}
            }
            if(action.payload.foodPreferences) {
                return {...state, foodPreferences: action.payload.foodPreferences}
            }
        default: 
            return state;
    }
  };

const savePaymentMethod = (state: ProfileState, action: AnyAction): ProfileState => {
    var paymentMethod = action.payload;
    var paymentMethodList = state.paymentMethodList;
    var selectedPaymentMethod = state.selectedPaymentMethod;
    for(var i in paymentMethodList) {
        var pm = paymentMethodList[i];
        if(paymentMethod.default && pm.id == paymentMethod.id) {
            selectedPaymentMethod = paymentMethod;
            pm.default = true;
        } else {
            pm.default = false;
        }
    }
    return {
        ...state,
        selectedPaymentMethod: selectedPaymentMethod,
        paymentMethodList: paymentMethodList
    }
}

const loadPaymentMethods = (state: ProfileState, action: AnyAction): ProfileState => {
    var actionPaymentMethods:PaymentMethod[] = action.payload;
    var statePaymentMethodList = state.paymentMethodList;
    var selectedPaymentMethod:PaymentMethod|undefined = undefined;
    for(var i in actionPaymentMethods) {
        var apm = actionPaymentMethods[i];
        for(var j in statePaymentMethodList) {
            var spm = statePaymentMethodList[j];
            if(spm.id == apm.id && spm.default) {
                apm.default = true;
                selectedPaymentMethod = apm;
                break;
            }
        }
        if(apm.default) {
            break;
        }
    }
    if(!selectedPaymentMethod && actionPaymentMethods.length > 0) {
        selectedPaymentMethod = actionPaymentMethods[0];
        selectedPaymentMethod.default = true;
    }
    return {
            ...state, 
            selectedPaymentMethod: selectedPaymentMethod,
            paymentMethodList: actionPaymentMethods
    }
}

const deletePaymentMethod = (state: ProfileState, action: AnyAction): ProfileState => {
    var paymentMethodList:Array<PaymentMethod> = Object.assign([], state.paymentMethodList);
    var selectedPaymentMethod = state.selectedPaymentMethod;
    ArrayUtils.remove<PaymentMethod>(paymentMethodList, action.payload, "cardNumber");
    if(paymentMethodList.length != 0) {
        paymentMethodList[0].default = true;
        selectedPaymentMethod = paymentMethodList[0];
    } else {
        selectedPaymentMethod = initialState.profileState.selectedPaymentMethod;
    }
    return {
        ...state, 
        selectedPaymentMethod: selectedPaymentMethod,
        paymentMethodList: paymentMethodList
    }
}