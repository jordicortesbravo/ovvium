import React from "react";
import { Text } from 'react-native';
import { AccessToken, LoginManager } from 'react-native-fbsdk';
import { AnyAction, bindActionCreators, Dispatch } from "redux";
import { GoogleSignin, statusCodes } from '@react-native-community/google-signin';
import appleAuth, {AppleAuthRequestOperation, AppleAuthRequestScope, AppleAuthCredentialState} from '@invertase/react-native-apple-authentication';
import { connect } from "react-redux";
import { NavigationProp } from "@react-navigation/core";
import { SocialProvider } from '../../../../model/enum/SocialProvider';
import { OvviumError } from '../../../../model/OvviumError';
import { Session } from '../../../../model/Session';
import { User } from "../../../../model/User";
import { AppState } from "../../../../store/State";
import { AxiosUtils } from '../../../../util/AxiosUtils';
import { AppScreens } from '../../../navigation/AppScreens';
import { BaseScreen, BaseScreenProps, baseMapDispatchToProps, baseMapStateToProps } from '../BaseScreen';
import { sendSocialLogin, register } from '../../../../actions/UserActions';
import { clearErrorActionCreator } from '../../../../actions/ExecutionActions';
import { RegisterView } from '../../../components/RegisterView/RegisterView';
import { SocialProfile } from "../../../../model/SocialProfile";
import analytics from '@react-native-firebase/analytics';
import { msg } from "../../../../services/LocalizationService";
import { errorDialog } from "../../../../util/WidgetUtils";

interface RegisterScreenProps extends BaseScreenProps {
  user?: User;
  session: Session;
  showIndicator: boolean;
  navigation: NavigationProp<any>;
  register: (username: string, password: string, name: string) => User;
  socialLogin: (socialProfile: SocialProfile) => User;
}

export class RegisterScreen extends BaseScreen<RegisterScreenProps, any> {

  static navigationOptions = {
    header: null
  };

  constructor(props: RegisterScreenProps) {
    super(props, {});
  }

  UNSAFE_componentWillMount() {
    GoogleSignin.configure();
  }

  render() {
    return <RegisterView onRegister={this.onRegister.bind(this)} 
                      onFacebookLogin={this.onFacebookLogin.bind(this)} 
                      onGoogleLogin={this.onGoogleLogin.bind(this)} 
                      onAppleLogin={this.onAppleLogin.bind(this)}
                      goToLoginView={this.goToLoginView.bind(this)} 
                      showIndicator={this.props.showIndicator} 
            />;
  }

  goToLoginView() {
    this.props.navigation.navigate(AppScreens.Login);
  }

  onRegister(email: string, password: string, name:string) {
    this.props.register(email, password, name);
    analytics().logSignUp({
      method: "Ovvium"
    });
    errorDialog({
      message: msg("login:activate:confirm") + email,
      screen: AppScreens.Register
    });
    this.goToLoginView();
  }

  onFacebookLogin() {
    LoginManager.logInWithPermissions(["public_profile", "email"]).then(
      (result: any) => {
        if (!result.isCancelled && result && result.grantedPermissions) {
            AccessToken.getCurrentAccessToken().then(
              (data: any) => {
                if(data != null) {
                  this.props.socialLogin({socialProvider: SocialProvider.FACEBOOK, token:data.accessToken} as SocialProfile);
                  analytics().logSignUp({
                    method: "Facebook"
                  });
                }
              }
            )
          }
          throw new OvviumError("Facebook Login Error");
        }, (error: any) => {
          throw new OvviumError("Facebook Login Error");
      });     
  }

  async onGoogleLogin() {
      try {
        await GoogleSignin.hasPlayServices();
        const userInfo = await GoogleSignin.signIn();
        if(userInfo.idToken == null) {
          throw new OvviumError("Google Login Error");
        }
        this.props.socialLogin({socialProvider:SocialProvider.GOOGLE, token:userInfo.idToken, email:userInfo.user.email, name:userInfo.user.name} as SocialProfile);
        analytics().logSignUp({
          method: "Google"
        });
      } catch (error) {
        if (error.code === statusCodes.SIGN_IN_CANCELLED) {
          // user cancelled the login flow
        } else if (error.code === statusCodes.IN_PROGRESS) {
          // operation (f.e. sign in) is in progress already
        } else {
          throw new OvviumError("Google Login Error");
        }
      }
  }

  async onAppleLogin() {
    const response = await appleAuth.performRequest({
      requestedOperation: AppleAuthRequestOperation.LOGIN,
      requestedScopes: [AppleAuthRequestScope.EMAIL, AppleAuthRequestScope.FULL_NAME],
    });
    if(response.user && response.identityToken && response.realUserStatus) {
      var id = response.user;
      var token = response.identityToken;
      var email = response.email;
      var name = this.recoverAppleName(response.fullName);
      this.props.socialLogin({socialProvider: SocialProvider.APPLE, id, token, email, name} as SocialProfile); 
      analytics().logSignUp({
        method: "Apple"
      });
    }
  }

  recoverAppleName(fullName: any) {
    if(fullName == null) {
      return null;
    } else {
      var givenName = fullName.givenName;
      // var middleName = fullName.middleName;
      var familyName = fullName.familyName;
      return givenName + " " + familyName;
    }
  }
}

function mapStateToProps(state: AppState): RegisterScreenProps {
  return baseMapStateToProps(state, {
    user: state.sessionState.user,
    session: state.sessionState.session,
    error: state.executionState.errors[AppScreens.Register],
    showIndicator: state.executionState.showIndicator,
    screen: AppScreens.Register
  } as RegisterScreenProps);
}

function mapDispatchToProps(dispatch: Dispatch<AnyAction>) {
  return baseMapDispatchToProps(dispatch,
    {
      register: register,
      socialLogin: sendSocialLogin,
      clearError: clearErrorActionCreator
    }
  );
}

const RegisterContainer = connect(
  mapStateToProps,
  mapDispatchToProps
)(RegisterScreen);

export { RegisterContainer };
