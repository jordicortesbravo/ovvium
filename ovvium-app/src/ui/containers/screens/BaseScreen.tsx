import React from 'react';
import { NavigationProp, RouteProp } from "@react-navigation/core";
import { AnyAction, bindActionCreators, Dispatch } from 'redux';
import { clearErrorActionCreator } from '../../../actions/ExecutionActions';
import { AppState } from '../../../store/State';
import { errorDialog } from '../../../util/WidgetUtils';

export interface BaseScreenProps {
    error: any;
    screen: string;
    navigation: NavigationProp<any>;
    route: RouteProp<any, any>;
    clearError: (screen: string) => void;
}

export interface BaseScreenState {
    errorDialogOpened?: boolean;
}

export class BaseScreen<P extends BaseScreenProps, S extends BaseScreenState> extends React.Component<P, S> {

    public readonly state: Readonly<S> = {
        errorDialogOpened: false
      } as S

    constructor(props: P, state?: S) {
        super(props);
        if(state) {
            this.state = state;
        }
    }

    UNSAFE_componentWillReceiveProps(newProps: P) {
        if(newProps.error && !this.state.errorDialogOpened) {
            this.setState({errorDialogOpened: true})
            errorDialog({error: newProps.error, screen: this.props.route.name, clearError:this.onClearError.bind(this)});
        }
    }
    
    onClearError(screen:string) {
        this.setState({errorDialogOpened: false})
        this.props.clearError(screen);
    }
}

export function baseMapDispatchToProps(dispatch: Dispatch<AnyAction>, actionsCreators: any) {
    actionsCreators = {...actionsCreators, clearError: clearErrorActionCreator}
    return bindActionCreators(actionsCreators, dispatch);
}

export function baseMapStateToProps(state: AppState, props: any): any {
    return {...props, error:state.executionState.errors[props.screen]}
}