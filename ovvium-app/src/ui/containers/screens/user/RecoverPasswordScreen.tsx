import React from "react";
import { connect } from "react-redux";
import { AnyAction, Dispatch } from "redux";
import { clearErrorActionCreator } from '../../../../actions/ExecutionActions';
import { NavigationProp } from "@react-navigation/core";
import { recoverPassword } from "../../../../actions/UserActions";
import { msg } from '../../../../services/LocalizationService';
import { AppState } from "../../../../store/State";
import { dialog } from '../../../../util/WidgetUtils';
import { AppScreens } from "../../../navigation/AppScreens";
import { baseMapDispatchToProps, baseMapStateToProps, BaseScreen, BaseScreenProps, BaseScreenState } from '../BaseScreen';
import { StringUtils } from '../../../../util/StringUtils';
import { RecoverPasswordView } from '../../../components/RecoverPasswordView/RecoverPasswordView';

interface RecoverPasswordScreenState extends BaseScreenState{
  redirectedToLogin: boolean;
  navigation: NavigationProp<any>;
}

interface RecoverPasswordScreenProps extends BaseScreenProps {
  recoverPassword: (email:string) => void;
  passwordRecovered: boolean;
  showIndicator: boolean;
}

export class RecoverPasswordScreen extends BaseScreen<RecoverPasswordScreenProps, RecoverPasswordScreenState> {
  static navigationOptions = {
    header: null
  };

  constructor(props: RecoverPasswordScreenProps) {
    super(props), {redirectedToLogin: false};
  }

  UNSAFE_componentWillReceiveProps(props: RecoverPasswordScreenProps) {
    if(props.passwordRecovered && !this.state.redirectedToLogin) {
      this.setState({redirectedToLogin: true});
    }
  }

  render() {
    return <RecoverPasswordView
              goToLoginView={this.goToLoginView.bind(this)} 
              onRecoverPassword={this.onRecoverPassword.bind(this)}
              passwordRecovered={this.props.passwordRecovered}
              showIndicator={this.props.showIndicator} 
            />;
  }

  goToLoginView() {
    this.props.navigation.navigate(AppScreens.Login);
  }

  onRecoverPassword(email: string) {
    if(StringUtils.isBlank(email)) {
      dialog(null, msg("error:validation:blankEmail"), () => {
      }, false);  
    } else {
      dialog(null, msg("login:recoverPassword.confirmation"), () => {
        this.props.recoverPassword(email);
      }, true)
    }
  }
}

function mapStateToProps(state: AppState): RecoverPasswordScreenProps {
  return baseMapStateToProps(state, {
    passwordRecovered: state.sessionState.passwordRecovered,
    error: state.executionState.errors[AppScreens.RecoverPassword],
    showIndicator: state.executionState.showIndicator
  } as RecoverPasswordScreenProps);
}

function mapDispatchToProps(dispatch: Dispatch<AnyAction>) {
  return baseMapDispatchToProps(dispatch,
    {
      recoverPassword: recoverPassword,
      clearError: clearErrorActionCreator
    });
}

const RecoverPasswordContainer = connect(
  mapStateToProps,
  mapDispatchToProps
)(RecoverPasswordScreen);
export { RecoverPasswordContainer };

