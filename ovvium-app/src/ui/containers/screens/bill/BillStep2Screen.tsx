import React from 'react';
import { View } from 'react-native';
import { NavigationProp } from "@react-navigation/core";
import { connect } from 'react-redux';
import { AnyAction, bindActionCreators, Dispatch } from 'redux';
import { toggleUserToPayActionCreator } from '../../../../actions/BillActions';
import { Bill } from '../../../../model/Bill';
import { Order } from '../../../../model/Order';
import { User } from '../../../../model/User';
import { UserBill } from '../../../../model/UserBill';
import { AppState } from '../../../../store/State';
import { AppScreens } from '../../../navigation/AppScreens';
import { getUserOrders } from '../../../../services/BillService';
import { BillStep2View } from '../../../components/BillStep2View/BillStep2View';
import { headerStyles } from '../../../components/Header/style';


interface BillStep2ScreenProps {
    bill?: Bill;
    me: User;
    userBill: UserBill | Bill; 
    selectedUsers: User[];
    selectedOrders: Order[];
    navigation: NavigationProp<any>;
    toggleMember: (user: User) => void;
}

class BillStep2Screen extends React.Component<BillStep2ScreenProps> {

  static navigationOptions = {
    header: (
      <View style={headerStyles.emptyHeaderContainer}/>
    )
  };
  
  render() {
    if(!this.props.bill) {
      return <></>;
    }
    return <BillStep2View 
      bill={this.props.bill} 
      userBill={this.props.userBill} 
      me={this.props.me}
      toggleMember={this.props.toggleMember}
      selectedUsers={this.props.selectedUsers}
      selectedOrders={this.props.selectedOrders}
      goToTip={() => this.props.navigation.navigate(AppScreens.BillStep3)}
      goBack={() => this.props.navigation.navigate(AppScreens.BillStep1)}
      />;
  }
}

function mapStateToProps(state: AppState): BillStep2ScreenProps {
  return {
    bill: state.billState.bill,
    me: state.sessionState.user,
    userBill: state.billState.userBill,
    selectedUsers: state.billState.usersToPay,
    selectedOrders: getUserOrders(state.billState.bill, state.billState.usersToPay)
  } as BillStep2ScreenProps;
}

function mapDispatchToProps(dispatch: Dispatch<AnyAction>) {
  return bindActionCreators(
    {
      toggleMember: toggleUserToPayActionCreator
    },
    dispatch
  );
}

const BillStep2Container = connect(
  mapStateToProps,
  mapDispatchToProps
)(BillStep2Screen);

export default BillStep2Container;


