import React from "react";
import { View } from 'react-native';
import { connect } from 'react-redux';
import { AnyAction, Dispatch } from 'redux';
import { AppState } from '../../../../store/State';
import { ChangePasswordView } from "../../../components/ChangePasswordView/ChangePasswordView";
import { headerStyles } from '../../../components/Header/style';
import { baseMapDispatchToProps, baseMapStateToProps, BaseScreen, BaseScreenProps } from '../BaseScreen';
import { errorDialog } from "../../../../util/WidgetUtils";
import { AppScreens } from "../../../navigation/AppScreens";
import { msg } from "../../../../services/LocalizationService";
import { changePasswordAction } from "../../../../actions/UserProfileActions";
import { User } from "../../../../model/User";

interface ChangePasswordScreenProps extends BaseScreenProps {
  me: User;
  changePassword: (user: User, currentPassword: string, newPassword: string) => Promise<void>;
}

class ChangePasswordScreen extends BaseScreen<ChangePasswordScreenProps,any> {

  static navigationOptions = {
    header: (
      <View style={headerStyles.emptyHeaderContainer}/>
    )
  };

  render() {
    return <ChangePasswordView
              changePassword={(this.changePassword.bind(this))}
              goBack={() => this.props.navigation.goBack()}
            />
  }

  changePassword(currentPassword: string, newPassword: string) {
    this.props.changePassword(this.props.me, currentPassword, newPassword)
    .then(() => {
      this.props.navigation.goBack();
    }).catch((error) => {
      errorDialog({
        message: msg("error:validation:changePassword:wrongPassword"),
        screen: AppScreens.ChangePassword
      })
    })
  }

}

function mapStateToProps(state: AppState): ChangePasswordScreenProps {
  return baseMapStateToProps(state, {
    me: state.sessionState.user
  })
}

function mapDispatchToProps(dispatch: Dispatch<AnyAction>) {
  return baseMapDispatchToProps(dispatch, {
    changePassword: changePasswordAction
  });
}

const ChangePasswordContainer = connect(mapStateToProps, mapDispatchToProps)(ChangePasswordScreen);

export { ChangePasswordContainer };

