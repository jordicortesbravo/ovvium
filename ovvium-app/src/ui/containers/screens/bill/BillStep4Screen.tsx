import React from 'react';
import { View } from 'react-native';
import { connect } from 'react-redux';
import { AnyAction, bindActionCreators, Dispatch } from 'redux';
import { NavigationProp } from "@react-navigation/core";
import { clearErrorActionCreator } from '../../../../actions/ExecutionActions';
import { savePaymentMethodActionCreator, selectPaymentMethodAction } from '../../../../actions/UserProfileActions';
import { Bill } from '../../../../model/Bill';
import { PaymentMethod } from '../../../../model/PaymentMethod';
import { getDefaultPaymentMethod } from '../../../../services/UserProfileService';
import { AppState } from '../../../../store/State';
import BillStep4View from '../../../components/BillStep4View/BillStep4View';
import { headerStyles } from '../../../components/Header/style';
import { AppScreens } from '../../../navigation/AppScreens';


interface BillStep4ScreenProps {
    bill: Bill;
    paymentMethod: PaymentMethod;
    navigation: NavigationProp<any>;
    savePaymentMethod: (paymentMethod: PaymentMethod) => void;
    selectPaymentMethod: (paymentMethod: PaymentMethod) => void;
    clearError: (screen:string) => void;
}
interface BillStep4ScreenState {
  paymentMethod: PaymentMethod;
  confirmInterceptor?: () => void;
}

class BillStep4Screen extends React.Component<BillStep4ScreenProps, BillStep4ScreenState> {

  constructor(props: BillStep4ScreenProps) {
    super(props);
    this.state = {paymentMethod: props.paymentMethod}
  }

  static navigationOptions = {
    header: (
      <View style={headerStyles.emptyHeaderContainer}/>
    )
  };
  
  render() {
    return <BillStep4View
              bill={this.props.bill} 
              paymentMethod={this.state.paymentMethod}
              addRunConfirmInterceptor={interceptor => this.setState({confirmInterceptor: interceptor})}
              onSaveButtonPressed={() => this.state.confirmInterceptor!()}
              onSavePaymentMethod={this.onSavePaymentMethod.bind(this)}
              goBack={() => this.props.navigation.navigate(AppScreens.BillStep3)}
            />;
  }

  onSavePaymentMethod(paymentMethod: PaymentMethod) {
      this.setState({paymentMethod})
      paymentMethod.default = true;
      this.props.savePaymentMethod(paymentMethod);
      this.props.navigation.navigate(AppScreens.BillStep5);
  }
}

function mapStateToProps(state: AppState): BillStep4ScreenProps {
  var defaultPaymentMethod = getDefaultPaymentMethod(state.profileState.paymentMethodList);
  return {
    bill: state.billState.bill,
    paymentMethod: state.profileState &&  defaultPaymentMethod ? defaultPaymentMethod : {} as PaymentMethod
  } as BillStep4ScreenProps;
}

function mapDispatchToProps(dispatch: Dispatch<AnyAction>) {
  return bindActionCreators(
    {
      clearError: clearErrorActionCreator,
      savePaymentMethod: savePaymentMethodActionCreator,
      selectPaymentMethod: selectPaymentMethodAction
    },
    dispatch
  );
}

const BillStep4Container = connect(
  mapStateToProps,
  mapDispatchToProps
)(BillStep4Screen);

export default BillStep4Container;


