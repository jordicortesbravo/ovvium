import { AnyAction, Reducer } from "redux";
import { OnboardingActionType } from "../actions/OnboardingActions";
import { initialState, OnboardingState } from "../store/State";

export const onboardingStateReducer: Reducer<OnboardingState> = (state: OnboardingState = initialState.onboardingState, action: AnyAction): OnboardingState => {
    switch (action.type) {
        case OnboardingActionType.HIDE_ONBOARDING:
            return {...state, showOnboarding: false}
        case OnboardingActionType.HIDE_TRICK:
            var shownTricks = state.shownTricks;
            shownTricks.push(action.payload.trick);
            return {...state, shownTricks: shownTricks.slice()}
        default:
            return state;
        }
};
  
  