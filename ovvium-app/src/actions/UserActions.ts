import crashlytics from '@react-native-firebase/crashlytics';
import axios from "axios";
import { AnyAction, Dispatch } from "redux";
import { properties } from "../../resources/Properties";
import { RecoverPasswordResponse } from '../model/response/RecoverPasswordResponse';
import { RegisterResponse } from '../model/response/RegisterResponse';
import { Session } from "../model/Session";
import { SocialProfile } from "../model/SocialProfile";
import { User } from "../model/User";
import { AppScreens } from '../ui/navigation/AppScreens';
import { AxiosUtils } from "../util/AxiosUtils";
import { LoginResponse } from "./../model/response/LoginResponse";
import { createAction, withApiBaseUrl } from './BaseAction';
import { refreshBillActionCreator } from "./BillActions";
import { ExecutionActionType } from './ExecutionActions';
import { loadPaymentMethods, loadUserProfileData } from "./UserProfileActions";
import { CrashlyticsUtil } from '../util/CrashLyticsUtil';
import analytics from '@react-native-firebase/analytics';

export enum UserActionType {
  LOGIN_USER_SUCCESS = "LOGIN_USER_SUCCESS",
  LOGIN_USER_ERROR = "LOGIN_USER_ERROR",
  REGISTER_USER_SUCCESS = "REGISTER_USER_SUCCESS",
  REGISTER_USER_ERROR = "REGISTER_USER_ERROR",
  ACTIVATION_USER_SUCCESS = "ACTIVATION_USER_SUCCESS",
  ACTIVATION_USER_ERROR = "ACTIVATION_USER_ERROR",
  USER_REFRESH_TOKEN = "USER_REFRESH_TOKEN",
  LOGOUT_USER = "LOGOUT_USER",
  REMOVE_USER = "REMOVE_USER",
  RECOVER_PASSWORD = "RECOVER_PASSWORD",
  RECOVER_PASSWORD_SUCCESS = "RECOVER_PASSWORD_SUCCESS"
}

export const register = (email: string, password: string, name: string) => async (dispatch: Dispatch<AnyAction>) => { 
  try {
    dispatch(createAction(ExecutionActionType.SHOW_INDICATOR, undefined));
    var url = withApiBaseUrl(properties.user.register);
    analytics().logEvent("start_register", {
      email: email,
      name: name
    });
    let response = await axios.post<RegisterResponse>(url, {
        email: email,
        name: name,
        password: password
    });

    var user = new User({
      id: response.data.userId,
      email: email,
      name: name,
      enabled: false
    });
    analytics().logEvent("end_register", user);
    analytics().setUserId(user.id);
    dispatch(createAction(UserActionType.REGISTER_USER_SUCCESS, {user: user}));
    loadUserProfileData()(dispatch);
  } catch(error) {
    CrashlyticsUtil.recordError("Error in register", error);
    dispatch(createAction(ExecutionActionType.ADD_ERROR, {error, screen: AppScreens.Register}));
  } finally {
    dispatch(createAction(ExecutionActionType.HIDE_INDICATOR, undefined));
  }
}

export const activate = (email: string, activationCode: string) => async (dispatch: Dispatch<AnyAction>) => { 
  try {
    dispatch(createAction(ExecutionActionType.SHOW_INDICATOR, undefined));
    var url = withApiBaseUrl(properties.user.activate);
    analytics().logEvent("start_activate", {email, activationCode});
    let response = await axios.post<RegisterResponse>(url, {
        email: email,
        activationCode: activationCode
    });

    var user = new User({
      id: response.data.userId,
      email: email,
      name: "",
      enabled: true
    });
    analytics().logEvent("end_activate", user);
    analytics().setUserId(user.id);
    var session = Session.from(response.data.session);
    dispatch(createAction(UserActionType.ACTIVATION_USER_SUCCESS, {user: user, session: session}));
  } catch(error) {
    CrashlyticsUtil.recordError("Error in activate", error);
    dispatch(createAction(UserActionType.ACTIVATION_USER_ERROR, {}));
    dispatch(createAction(ExecutionActionType.ADD_ERROR, {error, screen: AppScreens.Activation}));
  } finally {
    dispatch(createAction(ExecutionActionType.HIDE_INDICATOR, undefined));
  }
}

export const recoverPassword = (email:string) => async (dispatch: Dispatch<AnyAction>) => { 
  try {
    dispatch(createAction(ExecutionActionType.SHOW_INDICATOR, undefined));
    var url = withApiBaseUrl(properties.user.recoverPassword);
    analytics().logEvent("start_recover_password", {email});
    await axios.post<RecoverPasswordResponse>(url, {
        email: email
    });
    analytics().logEvent("end_recover_password", {email});
    dispatch(createAction(UserActionType.RECOVER_PASSWORD_SUCCESS, {}));
    crashlytics().setUserEmail(email);
  } catch(error) {
    CrashlyticsUtil.recordError("Error in recoverPassword", error);
    dispatch(createAction(ExecutionActionType.ADD_ERROR, {error, screen: AppScreens.RecoverPassword}));
  } finally {
    dispatch(createAction(ExecutionActionType.HIDE_INDICATOR, undefined));
  }
};

export const sendLogin = (email: string, password: string) => async (dispatch: Dispatch<AnyAction>) => { 
  try {
    dispatch(createAction(ExecutionActionType.SHOW_INDICATOR, undefined));
    var url = withApiBaseUrl(properties.user.login);
    analytics().logEvent("start_login", {email});
    let response = await axios.post<LoginResponse>(url, {
        email: email,
        password: password
    });
    var user = new User(response.data.user);
    var session = Session.from(response.data.session);
    AxiosUtils.setGlobalAuthHeader(session.accessToken);
    analytics().logEvent("end_login", {email});
    analytics().setUserId(user.id);
    dispatch(createAction(UserActionType.LOGIN_USER_SUCCESS, {user: user, session: session}));
    loadUserProfileData()(dispatch);
    loadPaymentMethods()(dispatch);
    refreshBillActionCreator(user)(dispatch);
    crashlytics().setUserId(user.id);
  } catch(error) {
    CrashlyticsUtil.recordError("Error in sendLogin", error);
    if(error.code == 401 && error.message == "INACTIVE_USER") {
      error.code = 7001;
      dispatch(createAction(ExecutionActionType.ADD_ERROR, {error, screen: AppScreens.Login}));
    } else {
      dispatch(createAction(ExecutionActionType.ADD_ERROR, {error, screen: AppScreens.Login}));
    }
    dispatch(createAction(UserActionType.LOGIN_USER_ERROR, {}));
  } finally {
    dispatch(createAction(ExecutionActionType.HIDE_INDICATOR, undefined));
  }
};

export const sendSocialLogin = (socialProfile: SocialProfile) => async (dispatch: Dispatch<AnyAction>) => { 
  try {
    dispatch(createAction(ExecutionActionType.SHOW_INDICATOR, undefined));
    var url = withApiBaseUrl(properties.user.authorize);
    analytics().logEvent("start_social_login", {socialProfile});
    let response = await axios.post<LoginResponse>(url, socialProfile);
    var user = new User(response.data.user);
    var session = Session.from(response.data.session);
    AxiosUtils.setGlobalAuthHeader(session.accessToken);
    analytics().logEvent("end_social_login", {user});
    analytics().setUserId(user.id);
    dispatch(createAction(UserActionType.LOGIN_USER_SUCCESS, {user: user, session: session}));
    loadUserProfileData()(dispatch);
    loadPaymentMethods()(dispatch);
    refreshBillActionCreator(user)(dispatch);
    crashlytics().setUserId(user.id);
  } catch(error) {
    CrashlyticsUtil.recordError("Error in sendSocialLogin", error);
    dispatch(createAction(UserActionType.LOGIN_USER_ERROR, {}));
    dispatch(createAction(ExecutionActionType.ADD_ERROR, {error, screen: AppScreens.Login}));
  } finally {
    dispatch(createAction(ExecutionActionType.HIDE_INDICATOR, undefined));
  }
}

export const logout = () => (dispatch: Dispatch<AnyAction>) => {
  analytics().logEvent("logout");
  dispatch(createAction(UserActionType.LOGOUT_USER, {}));
}

export const removeUser = (user: User) => (dispatch: Dispatch<AnyAction>) => {
  analytics().logEvent("remove_user", {userId: user.id});
  var url = withApiBaseUrl(properties.user.profile);
  axios.delete(url).then((response) => {
    dispatch(createAction(UserActionType.REMOVE_USER, {}));
  })
}

