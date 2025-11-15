import { sendLogin } from 'app/actions/UserActions';
import { LoginForm } from 'app/components/LoginForm/LoginForm';
import { AppRoute } from 'app/containers/Router/AppRoute';
import { User } from 'app/model/User';
import { AppState } from 'app/reducers/RootReducer';
import * as React from 'react';
import { connect } from 'react-redux';
import { AnyAction, bindActionCreators, Dispatch } from 'redux';
import { BaseContainer, BaseContainerProps } from '../BaseContainer';
import { Header } from 'app/components/Header/Header';

import * as style from './style.css';
import { browserHistory } from 'app/App';


export interface LoginContainerProps extends BaseContainerProps {
  showIndicator: boolean;
  login: (username: string, password: string) => User;
}

class LoginContainer extends BaseContainer<LoginContainerProps, any> {

  render() {
    return (
      <div className={style.main}>
        <Header />
        <div className="container">
          <LoginForm
            onLogin={this.onLogin.bind(this)}
            error={this.props.error != null}
            onRecover={this.onRecoverClick.bind(this)}
            showIndicator={this.props.showIndicator}
          />
        </div>
      </div>
    )
  }

  onRecoverClick() {
    browserHistory.push(AppRoute.FORGOT_PASSWORD);
  }

  onLogin(username: string, password: string) {
    this.props.login(username, password);
  }
}

function mapStateToProps(state: AppState, ownProps: LoginContainerProps): LoginContainerProps {
  return {
    ...ownProps,
    error: state.executionState.errors[AppRoute.LOGIN],
    showIndicator: state.executionState.showIndicator
  } as LoginContainerProps;
}

function mapDispatchToProps(dispatch: Dispatch<AnyAction>) {
  return bindActionCreators(
    {
      login: sendLogin
    },
    dispatch
  );
}

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(LoginContainer);
