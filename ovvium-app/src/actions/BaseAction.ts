import { AnyAction } from 'redux';
import { properties } from '../../resources/Properties';

export interface BaseAction<T, P> extends AnyAction {
    type: T,
    payload: P
}

export function createAction<T, P>(type: T, payload: P): BaseAction<T,P> {
    return { type, payload };
}

export function withApiBaseUrl(path : string) {
    return properties.apiBaseUrl + path;
}

export function withStaticsBaseUrl(path : string) {
    return properties.staticsBaseUrl + path;
}