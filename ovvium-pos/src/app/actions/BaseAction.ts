import { AnyAction } from 'redux';
import { properties } from 'app/config/Properties';

export interface BaseAction<T, P> extends AnyAction {
    type: T,
    payload: P
}

export function createAction<T, P>(type: T, payload: P): BaseAction<T,P> {
    return { type, payload };
}


export function withBaseUrl(path : string) {
    return properties.baseUrl + path;
}

export function withStaticBaseUrl(path : string) {
    return properties.staticsBaseUrl + path;
}

