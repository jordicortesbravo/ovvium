import React from "react";
import { View } from 'react-native';
import { connect } from 'react-redux';
import { AnyAction, Dispatch } from 'redux';
import { NavigationProp } from "@react-navigation/core";
import { PaymentMethod } from '../../../../model/PaymentMethod';
import { AppState } from '../../../../store/State';
import { headerStyles } from '../../../components/Header/style';
import { AppScreens } from '../../../navigation/AppScreens';
import { baseMapDispatchToProps, baseMapStateToProps, BaseScreen, BaseScreenProps } from '../BaseScreen';
import { PaymentMethodsView } from '../../../components/PaymentMethodsView/PaymentMethodsView';
import { PaymentMethodType } from "../../../../model/enum/PaymentMethodType";

interface PaymentMethodsScreenProps extends BaseScreenProps {
  paymentMethodList: Array<PaymentMethod>;
  navigation: NavigationProp<any>;
}

class PaymentMethodsScreen extends BaseScreen<PaymentMethodsScreenProps, any> {

  static navigationOptions = {
    header: (
      <View style={headerStyles.emptyHeaderContainer}/>
    )
  };

  render() {
    return <PaymentMethodsView
              paymentMethodList={this.props.paymentMethodList}
              goBack={() => this.props.navigation.navigate(AppScreens.Profile)}
              onModifyPaymentMethod={(paymentMethod: PaymentMethod) =>{
                this.editPaymentMethod(paymentMethod, false);
              }}
              onAddPaymentMethod={() => {
                this.editPaymentMethod({id: "", name: "", cardNumber: "", cardType: "", type:PaymentMethodType.APP_CREDIT_CARD, expiration: "", brand:"", default:false} as PaymentMethod, true);
              }}
            />
  }

  editPaymentMethod(paymentMethod: PaymentMethod, isNewPaymentMethod: boolean) {
    this.props.navigation.navigate(AppScreens.EditPaymentMethod, {paymentMethod: paymentMethod, isNewPaymentMethod:isNewPaymentMethod});
  }
}

function mapStateToProps(state: AppState): PaymentMethodsScreenProps {
  return baseMapStateToProps(state, {
    paymentMethodList: state.profileState.paymentMethodList
  })
}

function mapDispatchToProps(dispatch: Dispatch<AnyAction>) {
  return baseMapDispatchToProps(dispatch, {
  });
}

const PaymentMethodsContainer = connect(mapStateToProps, mapDispatchToProps)(PaymentMethodsScreen);

export { PaymentMethodsContainer };

