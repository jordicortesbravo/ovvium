import { BillActionType, loadBillsActionCreator } from "app/actions/BillActions";
import { browserHistory } from 'app/App';
import { properties } from "app/config/Properties";
import { AppRoute } from 'app/containers/Router/AppRoute';
import { LoginResponse } from 'app/model/response/LoginResponse';
import { Session } from 'app/model/Session';
import { User } from 'app/model/User';
import { AxiosUtils } from "app/utils/AxiosUtils";
import { default as axios, default as Axios } from "axios";
import { AnyAction, Dispatch } from "redux";
import { createAction, withBaseUrl } from "./BaseAction";
import { ExecutionActionType } from "./ExecutionActions";
import { Customer } from "app/model/Customer";
import { loadProductsCreator } from "app/actions/ProductActions";
import { loadLocationsCreator } from "app/actions/LocationActions";

export enum UserActionsType {
  LOGIN_USER_SUCCESS = 'LOGIN_USER_SUCCESS',
  LOGOUT_USER = 'LOGOUT_USER',
  USER_REFRESH_TOKEN = 'USER_REFRESH_TOKEN',
  RECOVER_PASSWORD_SUCCESS = 'RECOVER_PASSWORD_SUCCESS',
}

export const sendLogin = (email: string, password: string) => async (
  dispatch: Dispatch<AnyAction>
) => {
  try {
    dispatch(createAction(ExecutionActionType.SHOW_INDICATOR, undefined));
    var url = withBaseUrl(properties.user.login);
    // TODO: Habria que enviar el origin POS para que el login dependa del tipo de usuario
    let response = await axios.post<LoginResponse>(url, {
      email: email,
      password: password
    });
    var user = new User(response.data.user);
    var session = Session.from(response.data.session);
    if (user.customerId === undefined) {
      throw new Error("User is not a Customer.")
    }
    AxiosUtils.setAuthHeaderToConfig(Axios.defaults.headers.common, session.accessToken);
    url = withBaseUrl(properties.customer.get).replace("{customerId}", user.customerId);
    var customerResponse = await axios.get<Customer>(url);
    let customer = customerResponse.data;
    // var customer = new Customer({id: user.customerId, name: user.customerName});
    dispatch(createAction(UserActionsType.LOGIN_USER_SUCCESS, { user: user, session: session }));
    dispatch(createAction(BillActionType.SET_CUSTOMER, customer));
    loadBillsActionCreator(customer)(dispatch);
    loadProductsCreator(customer)(dispatch);
    loadLocationsCreator(customer)(dispatch);
    browserHistory.push(AppRoute.TAKE_ORDER);
  } catch (error) {
    dispatch(createAction(ExecutionActionType.ADD_ERROR, { error, container: AppRoute.LOGIN }))
  } finally {
    dispatch(createAction(ExecutionActionType.HIDE_INDICATOR, undefined));
  }
};

export const recoverPassword = (email: string) => async (
  dispatch: Dispatch<AnyAction>
) => {
  try {
    dispatch(createAction(ExecutionActionType.SHOW_INDICATOR, undefined));
    var url = withBaseUrl(properties.user.recoverPassword);
    await axios.post<any>(url, {
      email: email
    });
    dispatch(createAction(UserActionsType.RECOVER_PASSWORD_SUCCESS, { }));
  } catch (error) {
    dispatch(createAction(ExecutionActionType.ADD_ERROR, { error, container: AppRoute.FORGOT_PASSWORD }))
  } finally {
    dispatch(createAction(ExecutionActionType.HIDE_INDICATOR, undefined));
  }
};

export const onLogoutActionCreator = () => (dispatch: Dispatch<AnyAction>) => {
  dispatch(createAction(UserActionsType.LOGOUT_USER, {}));
};
