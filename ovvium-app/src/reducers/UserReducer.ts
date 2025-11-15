import { AnyAction, Reducer } from "redux";
import { UserActionType } from "../actions/UserActions";
import { initialState, SessionState } from "../store/State";
import { AxiosUtils } from '../util/AxiosUtils';
import { ProfileActionType } from '../actions/UserProfileActions';

export const userStateReducer: Reducer<SessionState> = (state: SessionState = initialState.sessionState, action: AnyAction): SessionState => {
  switch (action.type) {
    case UserActionType.LOGIN_USER_SUCCESS:
    case UserActionType.REGISTER_USER_SUCCESS:
    case UserActionType.ACTIVATION_USER_SUCCESS:
      return {
        ...state,
        user: action.payload.user,
        session: action.payload.session,
        passwordRecovered: false
      };
    case ProfileActionType.UPDATE_USER_PROFILE_DATA: 
      return {...state, user: action.payload.user};
    case UserActionType.ACTIVATION_USER_ERROR:
      return {
        ...state,
        user: undefined,
        session: undefined,
      };
    case UserActionType.LOGIN_USER_ERROR:
      return { ...state, passwordRecovered: false };
    case UserActionType.USER_REFRESH_TOKEN:
      AxiosUtils.setGlobalAuthHeader(action.payload.accessToken);
      return {
        ...state,
        session: action.payload
      };
    case UserActionType.RECOVER_PASSWORD_SUCCESS:
      return {
        ...state,
        passwordRecovered: true
      };
    case UserActionType.REMOVE_USER:
    case UserActionType.LOGOUT_USER:
      AxiosUtils.setGlobalAuthHeader();
      return {
        ...state,
        user: undefined,
        session: undefined,
        passwordRecovered: false
      };
    default:
      return state;
  }
};
