import { AnyAction, Reducer, combineReducers } from "redux";
import AsyncStorage from '@react-native-community/async-storage';
import { UserActionType } from '../actions/UserActions';
import { AppState, initialState } from "../store/State";
import { productsStateReducer } from './ProductReducer';
import { billStateReducer } from './BillReducer';
import { userStateReducer } from './UserReducer';
import { executionStateReducer } from './ExecutionReducer';
import { profileStateReducer } from './UserProfileReducer';
import { cartStateReducer } from "./CartReducer";
import { onboardingStateReducer } from "./OnboardingReducer";
import { AxiosUtils } from "../util/AxiosUtils";

const topLevelCombinedReduersReducer = combineReducers({
    productsState: productsStateReducer,
    billState: billStateReducer,
    cartState: cartStateReducer,
    sessionState: userStateReducer,
    executionState: executionStateReducer,
    profileState: profileStateReducer,
    onboardingState: onboardingStateReducer
  });

export const rootReducer: Reducer<any> = (state: AppState|undefined = initialState, action: AnyAction): AppState => {
    if (action.type === UserActionType.LOGOUT_USER || action.type === UserActionType.REMOVE_USER) {
        AsyncStorage.removeItem("persist:root");
        AxiosUtils.setGlobalAuthHeader();
        state = undefined;
    }
    return topLevelCombinedReduersReducer(state, action);
};