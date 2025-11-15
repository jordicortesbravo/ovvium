import { AnyAction, Reducer } from 'redux';
import { initialState, LocationState } from 'app/store/AppState';
import { LocationActionType } from 'app/actions/LocationActions';
import { Location } from 'app/model/Location';

export const locationsStateReducer: Reducer<LocationState> = (
  state: LocationState = initialState.locationState,
  action: AnyAction
): LocationState => {
  switch (action.type) {
    case LocationActionType.LIST_LOCATIONS:
      var locations = action.payload.locations as Array<Location>;
      return {
        ...state,
        locations: locations
      };
  }
  return state;
};