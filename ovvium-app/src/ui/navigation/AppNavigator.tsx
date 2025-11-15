import * as React from "react";
import { msg } from "../../services/LocalizationService";
import { createStackNavigator } from '@react-navigation/stack';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import MultifamilyIcon, { IconFamily } from '../components/MultiFamilyIcon/MultifamilyIcon';
import BillStep1Container from '../containers/screens/bill/BillStep1Screen';
import BillStep2Container from '../containers/screens/bill/BillStep2Screen';
import BillStep3OtherTipContainer from '../containers/screens/bill/BillStep3OtherTipScreen';
import BillStep3Container from '../containers/screens/bill/BillStep3Screen';
import BillStep4Container from '../containers/screens/bill/BillStep4Screen';
import BillStep5PickPaymentMethodContainer from '../containers/screens/bill/BillStep5PickPaymentMethodScreen';
import BillStep5Container from '../containers/screens/bill/BillStep5Screen';
import { ProductDetailContainer } from "../containers/screens/product/ProductDetailScreen";
import ProductListContainer from "../containers/screens/product/ProductListScreen";
import { RateProductContainer } from '../containers/screens/product/RateProductScreen';
import { AllergensContainer } from '../containers/screens/profile/AllergensScreen';
import { EditPaymentMethodContainer } from '../containers/screens/profile/EditPaymentMethodScreen';
import { FoodPreferencesContainer } from '../containers/screens/profile/FoodPreferencesScreen';
import { PaymentMethodsContainer } from '../containers/screens/profile/PaymentMethodsScreen';
import ProfileContainer from "../containers/screens/user/ProfileScreen";
import CartIcon from '../containers/widgets/CartIcon';
import { AppColors } from "../styles/layout/AppColors";
import { AppScreens } from "./AppScreens";
import JoinBillContainer from "../containers/screens/bill/JoinBillScreen";
import JoinBillByQRContainer from "../containers/screens/bill/JoinBillByQRScreen";
import InvoiceContainer from "../containers/screens/bill/InvoiceScreen";
import { ConfigureProductContainer } from "../containers/screens/product/ConfigureProductScreen";
import { AppFonts } from "../styles/layout/AppFonts";
import { ProductDetailInsideGroupContainer } from "../containers/screens/product/ProductDetailInsideGroupScreen";
import { EditProfileContainer } from "../containers/screens/profile/EditProfileScreen";
import { ChangePasswordContainer } from "../containers/screens/profile/ChangePasswordScreen";

const ProductStack = createStackNavigator();

function ProductNavigator() {
  return (
    <ProductStack.Navigator screenOptions={{headerShown: false}}>
        <ProductStack.Screen name={AppScreens.Products} component={ProductListContainer}/>
        <ProductStack.Screen name={AppScreens.ProductDetail} component={ProductDetailContainer}/>
        <ProductStack.Screen name={AppScreens.RateProduct} component={RateProductContainer}/>
        <ProductStack.Screen name={AppScreens.ConfigureProduct} component={ConfigureProductContainer}/>
        <ProductStack.Screen name={AppScreens.ProductDetailInsideGroup} component={ProductDetailInsideGroupContainer}/>
    </ProductStack.Navigator>
  );
} 

/**************************************************/
/**************************************************/
const JoinBillStack = createStackNavigator();

function JoinBillNavigator() {
  return (
    <JoinBillStack.Navigator screenOptions={{headerShown: false}}>
        <JoinBillStack.Screen name={AppScreens.JoinBill} component={JoinBillContainer} />
        <JoinBillStack.Screen name={AppScreens.JoinBillByQR} component={JoinBillByQRContainer} />
    </JoinBillStack.Navigator>
  );
} 

/**************************************************/
/**************************************************/

const BillStack = createStackNavigator();

function BillNavigator() {
  return (
    <BillStack.Navigator screenOptions={{headerShown: false}}>
        <BillStack.Screen name={AppScreens.BillStep1} component={BillStep1Container}/>
        <BillStack.Screen name={AppScreens.BillStep2} component={BillStep2Container}/>
        <BillStack.Screen name={AppScreens.BillStep3} component={BillStep3Container}/>
        <BillStack.Screen name={AppScreens.BillStep3OtherTip} component={BillStep3OtherTipContainer}/>
        <BillStack.Screen name={AppScreens.BillStep4} component={BillStep4Container}/>
        <BillStack.Screen name={AppScreens.BillStep5} component={BillStep5Container}/>
        <BillStack.Screen name={AppScreens.BillStep5PickPaymentMethod} component={BillStep5PickPaymentMethodContainer}/>
        <BillStack.Screen name={AppScreens.Invoice} component={InvoiceContainer}/>
    </BillStack.Navigator>
  );
}  

/**************************************************/
/**************************************************/
const ProfileStack = createStackNavigator();

function ProfileNavigator() {
  return (
    <ProfileStack.Navigator screenOptions={{headerShown: false}}>
        <ProfileStack.Screen name={AppScreens.Profile} component={ProfileContainer}/>
        <ProfileStack.Screen name={AppScreens.PaymentMethods} component={PaymentMethodsContainer}/>
        <ProfileStack.Screen name={AppScreens.EditPaymentMethod} component={EditPaymentMethodContainer}/>
        <ProfileStack.Screen name={AppScreens.Allergens} component={AllergensContainer}/>
        <ProfileStack.Screen name={AppScreens.FoodPreferences} component={FoodPreferencesContainer}/>
        <ProfileStack.Screen name={AppScreens.EditProfile} component={EditProfileContainer}/>
        <ProfileStack.Screen name={AppScreens.ChangePassword} component={ChangePasswordContainer}/>
    </ProfileStack.Navigator>
  );
}

/**************************************************/
/**************************************************/
const AppTab = createBottomTabNavigator();

function AppNavigator(joinedToBill:boolean) {
    return  <AppTab.Navigator tabBarOptions={tabOptions} screenOptions={screenOptions} initialRouteName={msg('products:label')}>
              <AppTab.Screen  name={msg('bill:label')} component={BillNavigator} />
              <AppTab.Screen name={msg('products:label')} component={joinedToBill ? ProductNavigator : JoinBillNavigator} />
              <AppTab.Screen name={msg('profile:label')} component={ProfileNavigator} />
            </AppTab.Navigator>
}

const tabOptions = {
  showIcon: true,
  activeTintColor: AppColors.main,
  labelStyle: {
    fontFamily: AppFonts.regular,
    fontSize: 12
  }
}

const screenOptions = (routeConfig: any)  => ({
  tabBarIcon: (iconConfig: any) => {
    if (routeConfig.route.name === msg('bill:label')) {
      //@ts-ignore
      return <CartIcon color={iconConfig.color}/>;
    } else if (routeConfig.route.name === msg('products:label')) {
      return <MultifamilyIcon family={IconFamily.FEATHER} name="search" size={26} color={iconConfig.color} />;
    } else if(routeConfig.route.name === msg('profile:label')) {
      return <MultifamilyIcon family={IconFamily.FEATHER} name="user" size={26} color={iconConfig.color} />;
    }
    return null;
  }
})

export { AppNavigator };

