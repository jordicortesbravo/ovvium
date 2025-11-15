import { AxiosRequestConfig, default as Axios, default as axios } from "axios";
import { properties } from "../../resources/Properties";
import { createAction, withApiBaseUrl } from '../actions/BaseAction';
import { UserActionType } from "../actions/UserActions";
import { OvviumError } from '../model/OvviumError';
import { AppState } from '../store/State';
import { AxiosUtils } from './../util/AxiosUtils';
import { store } from "./ReduxConfig";

export function initAxios() {
  accessTokenInterceptor()
  refreshTokenInterceptor();
  AxiosUtils.setGlobalApiKeyHeader(properties.apiKey.ovviumApi)
}

function accessTokenInterceptor() {
  Axios.interceptors.request.use((config: AxiosRequestConfig) => {
    var session = store.getState().sessionState.session;
    if (session) {
      config.headers.Authorization = "Bearer " + session.accessToken;
    }
    return config;
  });
}

/**
 * When accessToken no longer valid, request a new one with refresh token and retry.
 * 
 * If refresh token is expired, should go to login page.
 */
function refreshTokenInterceptor() {

  const responseInterceptor = Axios.interceptors.response.use(
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
       * token refresh causes the 401 response.
       * Then, add interceptor again when finished.
       */
      Axios.interceptors.response.eject(responseInterceptor);

      var session = (store.getState() as AppState).sessionState.session;

      if (session) {
        return getNewTokenAndRetry(ovviumError, session.refreshToken);
      }
      return Promise.reject(ovviumError);
    }
  );

  function getNewTokenAndRetry(error: any, refreshToken?: string) {
    return axios
      .post(withApiBaseUrl(properties.user.refreshToken), {
        "refreshToken": refreshToken
      })
      .then(response => {
        store.dispatch(createAction(UserActionType.USER_REFRESH_TOKEN, response.data));
        if (!response.data || response.status !== 200) {
          return Promise.reject(new Error("Refresh token not valid."))
        }
        // Retry this call with this new access token
        AxiosUtils.setGlobalAuthHeader(response.data.accessToken);
        return Axios.request(error.config);
      })
      .catch(error => {
        store.dispatch(createAction(UserActionType.LOGOUT_USER, {}));
        return Promise.reject(error);
      }).finally(() => {
        refreshTokenInterceptor();
      });
  }
}
