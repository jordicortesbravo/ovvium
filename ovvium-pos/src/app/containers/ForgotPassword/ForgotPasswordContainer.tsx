import { AppRoute } from 'app/containers/Router/AppRoute';
import { AppState } from 'app/reducers/RootReducer';
import * as React from 'react';
import { connect } from 'react-redux';
import { AnyAction, bindActionCreators, Dispatch } from 'redux';
import { BaseContainer, BaseContainerProps } from '../BaseContainer';
import { Header } from 'app/components/Header/Header';
import { RecoverForm } from 'app/components/RecoverForm/RecoverForm';
import { recoverPassword } from 'app/actions/UserActions';


import * as style from './style.css';

export interface ForgotPasswordContainerProps extends BaseContainerProps {
  showIndicator: boolean;
  recover: (email: string) => void;
}

class ForgotPasswordContainer extends BaseContainer<ForgotPasswordContainerProps, any> {

  render() {
    return <div className={style.main}>
      <Header />
      <div className="container">
        <RecoverForm
          onRecover={this.onRecover.bind(this)}
          error={this.props.error != null}
          showIndicator={this.props.showIndicator}
        />
      </div>
    </div>
  }

  onRecover(email: string) {
    this.props.recover(email);
  }

}

function mapStateToProps(state: AppState, ownProps: ForgotPasswordContainerProps): ForgotPasswordContainerProps {
  return {
    ...ownProps,
    error: state.executionState.errors[AppRoute.FORGOT_PASSWORD],
    showIndicator: state.executionState.showIndicator
  } as ForgotPasswordContainerProps;
}

function mapDispatchToProps(dispatch: Dispatch<AnyAction>) {
  return bindActionCreators(
    {
      recover: recoverPassword
    },
    dispatch
  );
}

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ForgotPasswordContainer);
