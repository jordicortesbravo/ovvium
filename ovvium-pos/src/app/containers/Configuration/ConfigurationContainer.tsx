import { ConfigurationView } from 'app/components/Configuration/ConfigurationView';
import { AppState } from 'app/store/AppState';
import * as React from 'react';
import { connect } from 'react-redux';
import { AnyAction, Dispatch } from 'redux';
import { onLogoutActionCreator } from 'app/actions/UserActions';
import { Customer } from 'app/model/Customer';
import { ErrorSnackbar } from 'app/components/ErrorSnackbar/ErrorSnackbar';
import { BaseContainer, baseMapDispatchToProps, baseMapStateToProps } from './../BaseContainer';
import { BaseContainerProps, BaseContainerState } from 'app/containers/BaseContainer';
import { AppRoute } from '../Router/AppRoute';

interface ConfigurationContainerProps extends BaseContainerProps {
    customer: Customer;
    avatar: string | undefined;
    logout: () => void;
}

interface ConfigurationContainerState extends BaseContainerState {

}


class ConfigurationContainer extends BaseContainer<ConfigurationContainerProps, ConfigurationContainerState> {

    constructor(props) {
        super(props,
            {
                errorDialogOpened: false
            });
    }

    render() {
        return <>
            <ConfigurationView customer={this.props.customer} avatar={this.props.avatar} logout={this.props.logout.bind(this)} />
            <ErrorSnackbar show={this.state.errorDialogOpened} error={this.props.error} onClose={this.onClearError.bind(this)} />
        </>
    }

}

function mapStateToProps(state: AppState): ConfigurationContainerProps {
    return baseMapStateToProps(state, {
        customer: state.billState.customer,
        avatar: state.sessionState.user!.imageUri,
        route: AppRoute.CONFIG
    } as ConfigurationContainerProps);
}

function mapDispatchToProps(dispatch: Dispatch<AnyAction>) {
    return baseMapDispatchToProps(dispatch,
        {
            logout: onLogoutActionCreator
        }
    );
}

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ConfigurationContainer);
