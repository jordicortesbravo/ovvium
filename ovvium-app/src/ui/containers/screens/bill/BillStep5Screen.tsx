import React from 'react';
import { View, Vibration } from 'react-native';
import { connect } from 'react-redux';
import { AnyAction, bindActionCreators, Dispatch } from 'redux';
import { NavigationProp } from "@react-navigation/core";
import { refreshBillActionCreator, payActionCreator } from '../../../../actions/BillActions';
import { Bill } from '../../../../model/Bill';
import { PaymentMethod } from '../../../../model/PaymentMethod';
import { Customer } from '../../../../model/Customer';
import { Order } from '../../../../model/Order';
import { Tip } from '../../../../model/Tip';
import { User } from '../../../../model/User';
import { AppState } from '../../../../store/State';
import { AppScreens } from '../../../navigation/AppScreens';
import { headerStyles } from '../../../components/Header/style';
import { PaymentStatus } from '../../../../model/enum/PaymentStatus';
import BillStep5View from '../../../components/BillStep5View/BillStep5View';
import ReactNativeBiometrics from 'react-native-biometrics'
import { disableAutoLaunchNfcActionCreator, enableAutoLaunchNfcActionCreator } from '../../../../actions/ExecutionActions';

interface BillStep5ScreenProps {
    bill: Bill;
    customer: Customer;
    usersToPay: User[];
    ordersToPay: Order[];
    tip: Tip;
    me: User;
    paymentMethod: PaymentMethod;
    showIndicator: boolean;
    error?: any;
    navigation: NavigationProp<any>;
    refreshBill: (user: User) => void;
    pay: (orders: Order[], tip: Tip, paymentMethod: PaymentMethod, user: User, customer: Customer, bill: Bill) => Promise<boolean>;
    enableAutoLaunchNfc: () => void;
    disableAutoLaunchNfc: () => void;
}

interface BillStep5ScreenState { 
  paymentStatus?: PaymentStatus;
  disabledGoToInvoice?: boolean;
}

class BillStep5Screen extends React.Component<BillStep5ScreenProps, BillStep5ScreenState> {

  constructor(props: BillStep5ScreenProps) {
    super(props);
    this.state = {}
  }

  static navigationOptions = {
    header: (
      <View style={headerStyles.emptyHeaderContainer}/>
    )
  };

  UNSAFE_componentWillMount() {
    this.props.refreshBill(this.props.me);
  }

  render() {
    return <BillStep5View 
      customer={this.props.customer} 
      paymentMethod={this.props.paymentMethod}
      usersToPay={this.props.usersToPay}
      ordersToPay={this.props.ordersToPay}
      tip={this.props.tip}
      pay={this.payWithBiometrics.bind(this)}
      paymentStatus={this.state.paymentStatus}
      showIndicator={this.props.showIndicator}
      error={this.props.error}
      goToPickPaymentMethod={() => this.props.navigation.navigate(AppScreens.BillStep5PickPaymentMethod)}
      goToInvoice={() => {
        this.setState({disabledGoToInvoice: true})
        this.props.navigation.navigate(AppScreens.BillStep1)
      }}
      goBack={() => {
        if(this.state.paymentStatus == PaymentStatus.OK) {
          this.props.navigation.navigate(AppScreens.BillStep1);
        } else {
          this.props.navigation.goBack();
        }
      }} />;
  }

  pay() {
      this.setState({paymentStatus: PaymentStatus.PROCESSING})
      this.props.pay(this.props.ordersToPay, this.props.tip, this.props.paymentMethod, this.props.me, this.props.customer, this.props.bill)
      
      .then(result => {
        if(result) {
          this.props.disableAutoLaunchNfc();
          this.setState({paymentStatus: PaymentStatus.OK})
          setTimeout(() => {
            if(!this.state.disabledGoToInvoice) {
              this.props.navigation.navigate(AppScreens.BillStep1);
            }
          }, 2500);
          setTimeout(() => {
            this.props.enableAutoLaunchNfc();
          }, 5000);
        } else {
          this.setState({paymentStatus: PaymentStatus.KO})
        }
      }).catch(error => {
          this.setState({paymentStatus: PaymentStatus.KO})
      })
  }

  payWithBiometrics() {
    ReactNativeBiometrics.isSensorAvailable()
      .then((resultObject) => {
        const { available, biometryType } = resultObject
        if ( available && (biometryType === ReactNativeBiometrics.TouchID || biometryType === ReactNativeBiometrics.FaceID)) {
          ReactNativeBiometrics.simplePrompt({promptMessage: 'Confirm fingerprint'})
              .then((resultObject) => {
                const { success } = resultObject
                if (success) {
                  this.pay();
                }
              });
        } else {
          this.pay();
        }
    })
  }
}

  

function mapStateToProps(state: AppState): BillStep5ScreenProps {
  return {
    customer: state.billState.customer,
    bill: state.billState.bill,
    usersToPay: state.billState.usersToPay,
    ordersToPay: state.billState.ordersToPay,
    tip: state.billState.tip,
    me: state.sessionState.user,
    paymentMethod: state.profileState.selectedPaymentMethod,
    showIndicator: state.executionState.showIndicator,
    error: state.executionState.errors[AppScreens.BillStep5]
  } as BillStep5ScreenProps;
}

function mapDispatchToProps(dispatch: Dispatch<AnyAction>) {
  return bindActionCreators(
    {
      refreshBill: refreshBillActionCreator,
      pay: payActionCreator,
      disableAutoLaunchNfc: disableAutoLaunchNfcActionCreator,
      enableAutoLaunchNfc: enableAutoLaunchNfcActionCreator
    },
    dispatch
  );
}

const BillStep5Container = connect(
  mapStateToProps,
  mapDispatchToProps
)(BillStep5Screen);

export default BillStep5Container;


