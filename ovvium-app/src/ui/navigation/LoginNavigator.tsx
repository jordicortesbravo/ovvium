import * as React from 'react';
import { createStackNavigator } from '@react-navigation/stack';
import JoinBillByQRContainer from '../containers/screens/bill/JoinBillByQRScreen';
import JoinBillContainer from '../containers/screens/bill/JoinBillScreen';
import { ActivationContainer } from '../containers/screens/user/ActivationScreen';
import { LoginContainer } from "../containers/screens/user/LoginScreen";
import { RecoverPasswordContainer } from '../containers/screens/user/RecoverPasswordScreen';
import { RegisterContainer } from "../containers/screens/user/RegisterScreen";
import { AppScreens } from "./AppScreens";

const LoginStack = createStackNavigator();

export function LoginNavigator() {
  return (
    <LoginStack.Navigator screenOptions={{headerShown: false}}>
      <LoginStack.Screen name={AppScreens.Login} component={LoginContainer} />
      <LoginStack.Screen name={AppScreens.Register} component={RegisterContainer} />
      <LoginStack.Screen name={AppScreens.Activation} component={ActivationContainer} />
      <LoginStack.Screen name={AppScreens.RecoverPassword} component={RecoverPasswordContainer} />
    </LoginStack.Navigator>
  );
}
