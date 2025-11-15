import { properties } from 'app/config/Properties';
import { AppRoute } from 'app/containers/Router/AppRoute';
import { Customer } from 'app/model/Customer';
import { Location } from 'app/model/Location';
import { LocationResponse } from 'app/model/response/LocationResponse';
import axios from 'axios';
import { AnyAction, Dispatch } from 'redux';
import { createAction, withBaseUrl } from './BaseAction';
import { ExecutionActionType } from './ExecutionActions';

export enum LocationActionType {
  LIST_LOCATIONS = 'LIST_LOCATIONS'
}

export const loadLocationsCreator = (customer: Customer) => async (dispatch: Dispatch<AnyAction>) => {
  dispatch(createAction(ExecutionActionType.SHOW_INDICATOR, undefined));
  var locationsUrl = withBaseUrl(
    properties.locations.list.replace('{customerId}', customer.id.toString())
  );
  var locations = await axios
    .get<LocationResponse[]>(locationsUrl) //
    .then((response) => {
      return response.data.map((loc) => Location.from(loc));
    })
    .catch((error) => {
      dispatch(createAction(ExecutionActionType.ADD_ERROR, { error, route: AppRoute.TAKE_ORDER }));
    });
  dispatch(
    createAction(LocationActionType.LIST_LOCATIONS, {
      locations: locations
    })
  );
  dispatch(createAction(ExecutionActionType.HIDE_INDICATOR, undefined));
};
