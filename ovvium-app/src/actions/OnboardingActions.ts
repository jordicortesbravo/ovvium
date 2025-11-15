import { createAction } from './BaseAction';
import { Dispatch, AnyAction } from 'redux';
import AsyncStorage from '@react-native-community/async-storage';

export enum OnboardingActionType {
    HIDE_ONBOARDING = "HIDE_ONBOARDING",
    HIDE_TRICK = "HIDE_TRICK"
}

export const hideOnboardingActionCreator = () => async (dispatch: Dispatch<AnyAction>) => { 
    return dispatch(createAction(OnboardingActionType.HIDE_ONBOARDING, {}));
}

export const hideOnboardingTrickActionCreator = (trick: string) => async (dispatch: Dispatch<AnyAction>) => { 
    AsyncStorage.getItem("shownTricks")
    .then((item: string | null) => {
        var shownTricks;
        if(item == null) {
            shownTricks = [];
        } else {
            shownTricks = JSON.parse(item);
        }
        if(shownTricks.indexOf(trick) == -1) {
            shownTricks.push(trick);
        }
        AsyncStorage.setItem("shownTricks", JSON.stringify(shownTricks));
    })
    return dispatch(createAction(OnboardingActionType.HIDE_TRICK, {trick}));
}
