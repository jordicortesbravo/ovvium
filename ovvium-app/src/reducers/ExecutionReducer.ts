import { AnyAction, Reducer } from "redux";
import { ExecutionActionType } from '../actions/ExecutionActions';
import { ExecutionState, initialState } from "../store/State";

export const executionStateReducer: Reducer<ExecutionState> = (state: ExecutionState = initialState.executionState, action: AnyAction): ExecutionState => {
    switch (action.type) {
        case ExecutionActionType.ADD_ERROR:
            state.errors[action.payload.screen] = action.payload.error;
            var errors = Object.assign([], state.errors);
            return {...state, errors };
        case ExecutionActionType.CLEAR_ERROR:
            state.errors[action.payload] = undefined;
            return {...state, errors: Object.assign([], state.errors)};
        case ExecutionActionType.SHOW_INDICATOR:
            return {...state, showIndicator: true}
        case ExecutionActionType.HIDE_INDICATOR:
            return {...state, showIndicator: false}
        case ExecutionActionType.DISABLE_AUTO_LAUNCH_NFC:
            return {...state, autoLaunchNfc: false}
        case ExecutionActionType.ENABLE_AUTO_LAUNCH_NFC:
            return {...state, autoLaunchNfc: true}
        default:
            return state;
        }
    };
  
  