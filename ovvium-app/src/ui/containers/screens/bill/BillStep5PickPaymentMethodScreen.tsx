import React from 'react';
import { View } from 'react-native';
import { connect } from 'react-redux';
import { AnyAction, bindActionCreators, Dispatch } from 'redux';
import { NavigationProp } from "@react-navigation/core";
import { selectPaymentMethodAction } from '../../../../actions/UserProfileActions';
import { PaymentMethod } from '../../../../model/PaymentMethod';
import { AppState } from '../../../../store/State';
import { AppScreens } from '../../../navigation/AppScreens';
import { headerStyles } from '../../../components/Header/style';
import BillStep5PickPaymentMethodView from '../../../components/BillStep5PickPaymentMethodView/BillStep5PickPaymentMethodView';


interface BillStep5PickPaymentMethodScreenProps {
    paymentMethodList: Array<PaymentMethod>;
    navigation: NavigationProp<any>;
    selectPaymentMethod: (paymentMethod: PaymentMethod) => void;
}

class BillStep5PickPaymentMethodScreen extends React.Component<BillStep5PickPaymentMethodScreenProps> {

  static navigationOptions = {
    header: (
      <View style={headerStyles.emptyHeaderContainer}/>
    )
  };

  render() {
    return <BillStep5PickPaymentMethodView 
                paymentMethodList={this.props.paymentMethodList} 
                onPickPaymentMethod={this.selectPaymentMethod.bind(this)} 
                goBack={this.goBack.bind(this)}/>;
  }

  selectPaymentMethod(paymentMethod: PaymentMethod) {
    this.props.selectPaymentMethod(paymentMethod);
    this.goBack();
  }

  goBack() {
    this.props.navigation.navigate(AppScreens.BillStep5);
  }
}

function mapStateToProps(state: AppState): BillStep5PickPaymentMethodScreenProps {
  return {
    paymentMethodList: state.profileState.paymentMethodList,
  } as BillStep5PickPaymentMethodScreenProps;
}

function mapDispatchToProps(dispatch: Dispatch<AnyAction>) {
  return bindActionCreators(
    {
        selectPaymentMethod: selectPaymentMethodAction,
    },
    dispatch
  );
}

const BillStep5PickPaymentMethodContainer = connect(
  mapStateToProps,
  mapDispatchToProps
)(BillStep5PickPaymentMethodScreen);

export default BillStep5PickPaymentMethodContainer;


