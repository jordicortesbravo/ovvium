import { GoogleSignin, statusCodes } from '@react-native-community/google-signin';
import { NavigationProp } from "@react-navigation/core";
import React from "react";
import { AccessToken, LoginManager } from "react-native-fbsdk";
import { connect } from "react-redux";
import { AnyAction, bindActionCreators, Dispatch } from "redux";
import appleAuth, {AppleAuthRequestOperation, AppleAuthRequestScope, AppleAuthCredentialState} from '@invertase/react-native-apple-authentication';
import { clearErrorActionCreator } from '../../../../actions/ExecutionActions';
import { sendLogin, sendSocialLogin } from "../../../../actions/UserActions";
import { SocialProvider } from '../../../../model/enum/SocialProvider';
import { OvviumError } from '../../../../model/OvviumError';
import { User } from "../../../../model/User";
import { AppState } from "../../../../store/State";
import { LoginView } from '../../../components/LoginView/LoginView';
import { AppScreens } from "../../../navigation/AppScreens";
import { BaseScreen, BaseScreenProps, BaseScreenState } from '../BaseScreen';
import { SocialProfile } from '../../../../model/SocialProfile';
import analytics from '@react-native-firebase/analytics';

interface LoginScreenProps extends BaseScreenProps {
  showIndicator: boolean;
  navigation: NavigationProp<any>;
  login: (username: string, password: string) => User;
  recoverPassword: () => void;
  socialLogin: (ssocialProfile: SocialProfile) => User;
}

interface LoginScreenState extends BaseScreenState {
  profileDataLoaded: boolean;
}

export class LoginScreen extends BaseScreen<LoginScreenProps, LoginScreenState> {
  static navigationOptions = {
    header: null
  };

  constructor(props: LoginScreenProps) {
    super(props, {profileDataLoaded: false});
  }

  componentDidMount() {
    GoogleSignin.configure({
      webClientId: '865165425202-e3cg6u8ki3bvnfruvves7ok6a7ujglt2.apps.googleusercontent.com'
    });
  }

  render() {
    return <LoginView onLogin={this.onLogin.bind(this)} 
                      onFacebookLogin={this.onFacebookLogin.bind(this)} 
                      onGoogleLogin={this.onGoogleLogin.bind(this)} 
                      onAppleLogin={this.onAppleLogin.bind(this)}
                      goToRegisterView={this.goToRegisterView.bind(this)} 
                      onRecoverPassword={this.onRecoverPassword.bind(this)}
                      showIndicator={this.props.showIndicator} 
            />;
  }

  goToRegisterView() {
    this.props.navigation.navigate(AppScreens.Register);
  }

  onLogin(email: string, password: string) {
    this.props.login(email, password);
    analytics().logLogin({
      method: "Ovvium"
    });
  }

  onFacebookLogin() {
    LoginManager.logInWithPermissions(["public_profile", "email"]).then(
      (result: any) => {
        if (!result.isCancelled && result && result.grantedPermissions) {
            AccessToken.getCurrentAccessToken().then(
              (data: any) => {
                if(data != null) {
                  this.props.socialLogin({socialProvider: SocialProvider.FACEBOOK, token: data.accessToken} as SocialProfile);
                  analytics().logLogin({
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
        if(!userInfo.idToken && !userInfo.user.id) {
          throw new OvviumError("Google Login Error");
        }
          this.props.socialLogin({socialProvider: SocialProvider.GOOGLE, token: userInfo.idToken ? userInfo.idToken : userInfo.user.id, email: userInfo.user.email, name: userInfo.user.name} as SocialProfile);
          analytics().logLogin({
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
      analytics().logLogin({
        method: "Apple"
      });
    }

    // try {
    //   const credentialState = await appleAuth.getCredentialStateForUser(appleAuthRequestResponse.user);
    //   if (credentialState === AppleAuthCredentialState.AUTHORIZED) {
    //     var email = appleAuthRequestResponse.email;
    //     var name = this.recoverAppleName(appleAuthRequestResponse.fullName);
    //     this.props.socialLogin(SocialProvider.APPLE, appleAuthRequestResponse.identityToken!, email, name);
    //   }
    // } catch(error) {
    //   throw new OvviumError("Google Login Error");
    // }
  }

  onRecoverPassword() {
    this.props.navigation.navigate(AppScreens.RecoverPassword)
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

function mapStateToProps(state: AppState): LoginScreenProps {
  return {
    error: state.executionState.errors[AppScreens.Login],
    showIndicator: state.executionState.showIndicator
  } as LoginScreenProps;
}

function mapDispatchToProps(dispatch: Dispatch<AnyAction>) {
  return bindActionCreators(
    {
      login: sendLogin,
      socialLogin: sendSocialLogin,
      clearError: clearErrorActionCreator
    },
    dispatch
  );
}

const LoginContainer = connect(
  mapStateToProps,
  mapDispatchToProps
)(LoginScreen);
export { LoginContainer };

