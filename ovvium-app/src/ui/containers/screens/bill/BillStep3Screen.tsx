import React from 'react';
import { View } from 'react-native';
import { connect } from 'react-redux';
import { AnyAction, bindActionCreators, Dispatch } from 'redux';
import { NavigationProp } from "@react-navigation/core";
import { addTipToBillActionCreator } from '../../../../actions/BillActions';
import { Bill } from '../../../../model/Bill';
import { Customer } from '../../../../model/Customer';
import { Order } from '../../../../model/Order';
import { Tip } from '../../../../model/Tip';
import { User } from '../../../../model/User';
import { UserBill } from '../../../../model/UserBill';
import { AppState } from '../../../../store/State';
import { AppScreens } from '../../../navigation/AppScreens';
import { headerStyles } from '../../../components/Header/style';
import { PaymentMethod } from '../../../../model/PaymentMethod';
import { proposeTips } from '../../../../services/BillService';
import BillStep3View from '../../../components/BillStep3View/BillStep3View';


interface BillStep3ScreenProps {
    bill: Bill;
    me: User;
    userBill: UserBill,
    selectedOrders: Order[];
    customer: Customer;
    paymentMethodList: Array<PaymentMethod>;
    selectedTip: Tip;
    navigation: NavigationProp<any>;
    addTip: (bill: Bill, tip?:Tip) => void;
}

interface BillStep3ScreenState {
  selectedTip?: Tip;
}

class BillStep3Screen extends React.Component<BillStep3ScreenProps, BillStep3ScreenState> {

  constructor(props: BillStep3ScreenProps) {
    super(props);
    this.state = {selectedTip: this.proposeTips()[1]}
  }

  static navigationOptions = {
    header: (
      <View style={headerStyles.emptyHeaderContainer}/>
    )
  };
  
  render() {
    return <BillStep3View 
      bill={this.props.bill} 
      me={this.props.me}
      selectedOrders={this.props.selectedOrders}
      userBill={this.props.userBill}
      customer={this.props.customer}
      goBack={() => this.props.navigation.navigate(AppScreens.BillStep2)}
      goNext={this.goNext.bind(this)}
      goToOtherTip={() => {this.props.navigation.navigate(AppScreens.BillStep3OtherTip, {defaultTip: this.state.selectedTip})}}
      proposedTips={this.proposeTips()}
      selectedTip={this.state.selectedTip}
      addTip={this.addTip.bind(this)} />;
  }

  addTip(tip?: Tip) {
    if(tip) {
      tip.user = this.props.me;
    }
    this.setState({selectedTip: tip});
    this.props.addTip(this.props.bill, tip);
  }

  goNext() {
    this.props.addTip(this.props.bill, this.state.selectedTip);
    this.props.navigation.navigate(this.props.paymentMethodList.length > 0 ? AppScreens.BillStep5 : AppScreens.BillStep4);
  }

  proposeTips(): Tip[] {
    return proposeTips(this.props.selectedOrders, this.props.me)
  } 


}


function mapStateToProps(state: AppState): BillStep3ScreenProps {
  return {
    bill: state.billState.bill,
    me: state.sessionState.user,
    userBill: state.billState.userBill,
    selectedOrders: state.billState.ordersToPay,
    customer: state.billState.bill ? state.billState.bill.customer : state.billState.customer,
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

const BillStep3Container = connect(
  mapStateToProps,
  mapDispatchToProps
)(BillStep3Screen);

export default BillStep3Container;


