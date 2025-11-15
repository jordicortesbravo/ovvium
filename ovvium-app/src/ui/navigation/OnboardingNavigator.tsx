import { createStackNavigator } from '@react-navigation/stack';
import * as React from 'react';
import AppOnboardingSlider from '../containers/widgets/AppOnboardingSlider';
import { AppScreens } from "./AppScreens";

const OnboardingStack = createStackNavigator();

export function OnboardingNavigator() {
  return (
    <OnboardingStack.Navigator screenOptions={{headerShown: false}}>
      <OnboardingStack.Screen name={AppScreens.Onboarding} component={AppOnboardingSlider} />
    </OnboardingStack.Navigator>
  );
}
