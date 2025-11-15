
import { Dispatch, AnyAction, bindActionCreators } from 'redux';
import { clearErrorActionCreator } from '../actions/ExecutionActions';
import { RouteComponentProps } from 'react-router';
import { Component } from 'react';
import { AppState } from 'app/store/AppState';
import { store } from '../config/ReduxConfig';
import { AxiosUtils } from 'app/utils/AxiosUtils';

export interface BaseContainerProps extends RouteComponentProps<void> {
    error?: any;
    route: string;
    clearError: (route: string) => void;
}

export interface BaseContainerState {
    errorDialogOpened: boolean;
}

export class BaseContainer<P extends BaseContainerProps, S extends BaseContainerState> extends Component<P, S> {

    public state: Readonly<S> = {
        errorDialogOpened: false
    } as S

    constructor(props: P, state: S) {
        super(props);
        this.state = state
        if (store.getState().sessionState.session) {
            AxiosUtils.setGlobalAuthHeader(store.getState().sessionState.session!.accessToken);
        }
    }

    UNSAFE_componentWillReceiveProps(newProps: P) {
        if (newProps.error && !this.state.errorDialogOpened) {
            this.setState({ errorDialogOpened: true });
        }
    }

    onClearError() {
        this.setState({ errorDialogOpened: false });
    }
}

export function baseMapDispatchToProps(dispatch: Dispatch<AnyAction>, actionsCreators: any) {
    actionsCreators = { ...actionsCreators, clearError: clearErrorActionCreator }
    return bindActionCreators(actionsCreators, dispatch);
}

export function baseMapStateToProps(state: AppState, props: any): any {
    return { ...props, error: state.executionState.errors[props.route] }
}