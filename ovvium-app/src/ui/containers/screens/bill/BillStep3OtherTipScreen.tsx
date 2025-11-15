import React from 'react';
import { View } from 'react-native';
import { NavigationProp, RouteProp, Route } from "@react-navigation/core";
import { connect } from 'react-redux';
import { AnyAction, bindActionCreators, Dispatch } from 'redux';
import { addTipToBillActionCreator } from '../../../../actions/BillActions';
import { Bill } from '../../../../model/Bill';
import { Customer } from '../../../../model/Customer';
import { Order } from '../../../../model/Order';
import { PaymentMethod } from '../../../../model/PaymentMethod';
import { Tip } from '../../../../model/Tip';
import { User } from '../../../../model/User';
import { UserBill } from '../../../../model/UserBill';
import { AppState } from '../../../../store/State';
import { AppScreens } from '../../../navigation/AppScreens';
import BillStep3OtherTipView from '../../../components/BillStep3OtherTipView/BillStep3OtherTipView.';
import { headerStyles } from '../../../components/Header/style';



interface BillStep3ScreenProps {
    bill: Bill;
    me: User;
    userBill: UserBill,
    selectedOrders: Order[];
    customer: Customer;
    paymentMethodList: Array<PaymentMethod>;
    navigation: NavigationProp<any>;
    route: Route<string>;
    addTip: (bill: Bill, tip?:Tip) => void;
}

class BillStep3OtherTipScreen extends React.Component<BillStep3ScreenProps> {

  static navigationOptions = {
    header: (
      <View style={headerStyles.emptyHeaderContainer}/>
    )
  };
  
  render() {
    var defaultTip = this.props.route.params!['defaultTip'];
    return <BillStep3OtherTipView 
      bill={this.props.bill} 
      me={this.props.me}
      selectedOrders={this.props.selectedOrders}
      userBill={this.props.userBill}
      customer={this.props.customer}
      defaultTip={defaultTip}
      goBack={() => this.props.navigation.navigate(AppScreens.BillStep3)}
      addTip={this.addTip.bind(this)} />;
  }

  addTip(tip?: Tip) {
    if(tip) {
      tip.user = this.props.me;
    }
    this.props.addTip(this.props.bill, tip);
    this.props.navigation.navigate(this.props.paymentMethodList.length > 0 ? AppScreens.BillStep5 : AppScreens.BillStep4);
  }
}

function mapStateToProps(state: AppState): BillStep3ScreenProps {
  return {
    bill: state.billState.bill,
    me: state.sessionState.user,
    userBill: state.billState.userBill,
    selectedOrders: state.billState.ordersToPay,
    customer: state.billState.customer,
    paymentMethodList: state.profileState.paymentMethodList
  } as BillStep3ScreenProps;
}

function mapDispatchToProps(dispatch: Dispatch<AnyAction>) {
  return bindActionCreators(
    {
      addTip: addTipToBillActionCreator
    },
    dispatch
  );
}

const BillStep3OtherTipContainer = connect(
  mapStateToProps,
  mapDispatchToProps
)(BillStep3OtherTipScreen);

export default BillStep3OtherTipContainer;


