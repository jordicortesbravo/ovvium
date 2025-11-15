import { AnyAction, Reducer } from "redux";
import { ExecutionActionType } from '../actions/ExecutionActions';
import { ExecutionState, initialState } from "../store/AppState";

export const executionStateReducer: Reducer<ExecutionState> = (state: ExecutionState = initialState.executionState, action: AnyAction): ExecutionState => {
    switch (action.type) {
        case ExecutionActionType.ADD_ERROR:
            state.errors[action.payload.container] = action.payload.error;
            var errors = Object.assign([], state.errors);
            return {...state, errors };
        case ExecutionActionType.CLEAR_ERROR:
            delete state.errors[action.payload];
            return {...state, errors: Object.assign([], state.errors)};
        case ExecutionActionType.SHOW_INDICATOR:
            return {...state, showIndicator: true}
        case ExecutionActionType.HIDE_INDICATOR:
            return {...state, showIndicator: false}
            case ExecutionActionType.CHANGE_THEME:
            return {
              ...state,
              theme: action.payload.theme
            };
        default:
            return state;
        }
    };
  
  