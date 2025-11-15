import { createAction } from './BaseAction';
import { Dispatch, AnyAction } from 'redux';
import { AppRoute } from 'app/containers/Router/AppRoute';

export enum ExecutionActionType {
    SHOW_INDICATOR = "SHOW_INDICATOR",
    HIDE_INDICATOR = "HIDE_INDICATOR",
    ADD_ERROR = "ADD_ERROR",
    CLEAR_ERROR = "CLEAR_ERROR",
    CHANGE_THEME = 'CHANGE_THEME',
    REGISTER_EXECUTOR = 'REGISTER_EXECUTOR',
    UNREGISTER_EXECUTOR = 'UNREGISTER_EXECUTOR'
}

export const clearErrorActionCreator = (route: AppRoute) => async (dispatch: Dispatch<AnyAction>) => { 
    return dispatch(createAction(ExecutionActionType.CLEAR_ERROR, route));
}

export const createErrorActionCreator = (route: AppRoute, error: any) => async (dispatch: Dispatch<AnyAction>) => { 
    return dispatch(createAction(ExecutionActionType.ADD_ERROR, {error, route}));
}

export const onChangeThemeCreator = (theme: 'classic' | 'dark') => (dispatch: Dispatch<AnyAction>) => {
    dispatch(createAction(ExecutionActionType.CHANGE_THEME, {theme}));
};

export const registerExecutor = (jobId: string, callback: () => void, timeout:number) => (dispatch: Dispatch<AnyAction>) => {
    dispatch(createAction(ExecutionActionType.REGISTER_EXECUTOR, {jobId, callback, timeout}));
}

export const unregisterExecutor = (jobId: string) => (dispatch: Dispatch<AnyAction>) => {
    dispatch(createAction(ExecutionActionType.UNREGISTER_EXECUTOR, jobId));
}

