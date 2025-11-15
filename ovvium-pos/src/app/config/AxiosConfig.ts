import { default as Axios, default as axios } from "axios";
import { createAction, withBaseUrl } from '../actions/BaseAction';
import { UserActionsType } from "../actions/UserActions";

import { OvviumError } from './../model/OvviumError';
import { store } from "app/config/ReduxConfig";
import { properties } from "app/config/Properties";
import { AxiosUtils } from "app/utils/AxiosUtils";
import { browserHistory } from 'app/App';
import { AppRoute } from 'app/containers/Router/AppRoute';
import { AppState } from "app/reducers";

export function initAxios() {
  axiosRefreshTokenInterceptor();
  AxiosUtils.setGlobalApiKeyHeader(properties.apiKey.ovviumApi);
}

/**
 * When accessToken no longer valid, request a new one with refresh token and retry.
 * 
 * If refresh token is expired, should go to login page.
 */
function axiosRefreshTokenInterceptor() {

  const interceptor = Axios.interceptors.response.use(
    response => response,
    error => {
      var ovviumError = new OvviumError(error);
      // Reject promise if usual error
      if (!error.response || ![401,403].includes(error.response.status)) {
        return Promise.reject(ovviumError);
      }

      /*
       * When response code is 401, try to refresh the token.
       * Eject the interceptor so it doesn't loop in case
       * token refresh causes the 401 response. Then, add interceptor again when finished.
       */
      Axios.interceptors.response.eject(interceptor);

      var session = (store.getState() as AppState).sessionState.session;

      if (session) {
        return getNewTokenAndRetry(ovviumError, session.refreshToken);
      }
      return Promise.reject(ovviumError);
    }
  );

  function getNewTokenAndRetry(error: any, refreshToken?: string) {
    return axios
      .post(withBaseUrl(properties.user.refreshToken), {
        "refreshToken": refreshToken
      })
      .then(response => {
        store.dispatch(createAction(UserActionsType.USER_REFRESH_TOKEN, response.data));
        if (!response.data || response.status !== 200) {
          return Promise.reject(new Error("Refresh token failed."));
        }
        console.log("Refreshed token.");
        // Retry this call with this new access token
        AxiosUtils.setAuthHeaderToConfig(error.config.headers, response.data.accessToken);
        return Axios.request(error.config);
      })
      .catch(error => {
        store.dispatch(createAction(UserActionsType.LOGOUT_USER, {}));
        browserHistory.push(AppRoute.LOGIN);
        return Promise.reject(error);
      }).finally(() => {
        axiosRefreshTokenInterceptor();
      });
  }
}
