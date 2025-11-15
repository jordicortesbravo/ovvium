import { AnyAction, Reducer } from 'redux';
import { SessionState, initialState } from 'app/store/AppState';
import { UserActionsType } from 'app/actions/UserActions';
import { AxiosUtils } from 'app/utils/AxiosUtils';

export const userStateReducer: Reducer<SessionState> = (
  state: SessionState = initialState.sessionState,
  action: AnyAction
): SessionState => {
  switch (action.type) {
    case UserActionsType.LOGIN_USER_SUCCESS:
      return {
        ...state,
        user: action.payload.user,
        session: action.payload.session,
        isAuthenticated: true
      };
    case UserActionsType.LOGOUT_USER:
      // rootReducer will return empty state here
      return {
        isAuthenticated: false,
      };
    case UserActionsType.USER_REFRESH_TOKEN:
      AxiosUtils.setGlobalAuthHeader(action.payload.accessToken);
      return {
        ...state,
        session: action.payload
      };
  }
  return state;
};
