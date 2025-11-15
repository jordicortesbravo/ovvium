import { createAction } from './BaseAction';
import { Dispatch, AnyAction } from 'redux';

export enum ExecutionActionType {
    SHOW_INDICATOR = "SHOW_INDICATOR",
    HIDE_INDICATOR = "HIDE_INDICATOR",
    ADD_ERROR = "ADD_ERROR",
    CLEAR_ERROR = "CLEAR_ERROR",
    ENABLE_AUTO_LAUNCH_NFC = "ENABLE_AUTO_LAUNCH_NFC",
    DISABLE_AUTO_LAUNCH_NFC = "DISABLE_AUTO_LAUNCH_NFC",
}

export const clearErrorActionCreator = (screen: string) => async (dispatch: Dispatch<AnyAction>) => { 
    return dispatch(createAction(ExecutionActionType.CLEAR_ERROR, screen));
}

export const createErrorActionCreator = (screen: string, error: any) => async (dispatch: Dispatch<AnyAction>) => { 
    return dispatch(createAction(ExecutionActionType.ADD_ERROR, {error, screen}));
}

export const enableAutoLaunchNfcActionCreator = () => async (dispatch: Dispatch<AnyAction>) => { 
    return dispatch(createAction(ExecutionActionType.ENABLE_AUTO_LAUNCH_NFC, undefined));
}

export const disableAutoLaunchNfcActionCreator = () => async (dispatch: Dispatch<AnyAction>) => { 
    return dispatch(createAction(ExecutionActionType.DISABLE_AUTO_LAUNCH_NFC, undefined));
}
