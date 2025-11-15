import { AnyAction, Reducer, combineReducers } from "redux";
import { AppState, initialState } from 'app/store/AppState';
import { userStateReducer } from './UserReducer';
import { executionStateReducer } from './ExecutionReducer';
import { productStateReducer } from './ProductReducer';
import { locationsStateReducer } from './LocationsReducer';
import { billStateReducer } from './BillReducer';
import storage from 'redux-persist/lib/storage';
import { AxiosUtils } from "app/utils/AxiosUtils";
import { UserActionsType } from "app/actions/UserActions";
import { invoicesStateReducer } from './InvoicesReducer';

export { AppState };

export const topLevelCombinedReducersReducer = combineReducers<AppState>({
    sessionState: userStateReducer,
    executionState: executionStateReducer,
    billState: billStateReducer,
    invoicesState: invoicesStateReducer,
    productState: productStateReducer,
    locationState: locationsStateReducer,
});

export const rootReducer: Reducer<any> = (state: AppState | undefined = initialState, action: AnyAction): AppState => {
    if (action.type == UserActionsType.LOGOUT_USER) {
        storage.removeItem("persist:root")
        AxiosUtils.setGlobalAuthHeader();
        state = undefined; // reset whole state
    }
    return topLevelCombinedReducersReducer(state, action);
};