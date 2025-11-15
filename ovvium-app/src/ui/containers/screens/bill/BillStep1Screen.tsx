import React from 'react';
import { View } from 'react-native';
import { NavigationProp } from "@react-navigation/core";
import { connect } from 'react-redux';
import { AnyAction, Dispatch } from 'redux';
import { filterBillDetailActionCreator, refreshBillActionCreator, createOrders, getInvoicesActionCreator } from '../../../../actions/BillActions';
import { Bill } from '../../../../model/Bill';
import { Customer } from '../../../../model/Customer';
import { Tip } from '../../../../model/Tip';
import { User } from '../../../../model/User';
import { UserBill } from '../../../../model/UserBill';
import { mapBillByUser } from '../../../../services/BillService';
import { AppState, CartState } from '../../../../store/State';
import { AppScreens } from '../../../navigation/AppScreens';
import { baseMapDispatchToProps, baseMapStateToProps, BaseScreen, BaseScreenProps } from '../BaseScreen';
import BillStep1View from '../../../components/BillStep1View/BillStep1View';
import { headerStyles } from '../../../components/Header/style';
import { Invoice } from '../../../../model/Invoice';
import { Order } from '../../../../model/Order';
import { addToCartActionCreator, removeFromCartActionCreator } from '../../../../actions/CartActions';
import { Product } from '../../../../model/Product';
import { InvoicePage } from '../../../../model/response/InvoicePage';


interface BillStep1ScreenProps extends BaseScreenProps {
    cart: CartState;
    bill?: Bill;
    invoice?: Invoice;
    invoicePage?: InvoicePage;
    me: User;
    userBillList: UserBill[];
    customer: Customer;
    tip?: Tip;
    navigation: NavigationProp<any>;
    filterBillDetail: (userBill: UserBill | Bill) => void;
    refreshBill: (bill:Bill, user: User) => void;
    createOrders: (bill: Bill, user:User, orders:Order[]) => void;
    addProductToCart: (product: Product, user: User) => void;
    removeProductFromCart: (product: Product) => void;
    loadInvoiceHistory: (page: number) => void;
}

class BillStep1Screen extends BaseScreen<BillStep1ScreenProps, any> {

  static navigationOptions = {
    header: (
      <View style={headerStyles.emptyHeaderContainer}/>
    )
  };
  
  render() {
    return <BillStep1View 
      onAddProductToCart={this.addProductToCart.bind(this)}
      onRemoveProductFromCart={this.props.removeProductFromCart}
      refreshBill={this.refreshBill.bind(this)}
      loadInvoiceHistory={this.props.loadInvoiceHistory}
      cart={this.props.cart}
      bill={this.props.bill} 
      invoice={this.props.invoice}
      invoicePage={this.props.invoicePage}
      userBillList={this.props.userBillList} 
      me={this.props.me} 
      tip={this.props.tip}
      onPressInvoice={(invoice: Invoice) => this.props.navigation.navigate(AppScreens.Invoice, {invoice})}
      onSelectBill={this.onSelectBill.bind(this)}
      onConfirmCart={this.onConfirmCart.bind(this)}
    />;
  }

  onConfirmCart() {
    if(this.props.bill && this.props.cart.orders.length > 0) {
      this.props.createOrders(this.props.bill, this.props.me, this.props.cart.orders);
    }
  }


  onSelectBill(bill: UserBill | Bill) {
    this.props.filterBillDetail(bill);
    this.props.navigation.navigate(AppScreens.BillStep2);
  }

  refreshBill() {
    const {bill, me} = this.props;
    this.props.refreshBill(bill!, me);
  }

  addProductToCart(product: Product) {
    this.props.addProductToCart(product, this.props.me);
  }
}

function mapStateToProps(state: AppState): BillStep1ScreenProps {
  return baseMapStateToProps(state, {
    cart: state.cartState,
    bill: state.billState.bill,
    invoice: state.billState.lastInvoice,
    invoicePage: state.billState.invoicePage,
    me: state.sessionState.user,
    userBillList: mapBillByUser(state.billState.bill, state.sessionState.user as User),
    customer: state.billState.customer,
    tip: state.billState.tip,
    screen: AppScreens.BillStep1
  });
}

function mapDispatchToProps(dispatch: Dispatch<AnyAction>) {
  return baseMapDispatchToProps(dispatch, {
    filterBillDetail: filterBillDetailActionCreator,
    refreshBill: refreshBillActionCreator,
    createOrders: createOrders,
    addProductToCart: addToCartActionCreator,
    removeProductFromCart: removeFromCartActionCreator,
    loadInvoiceHistory: getInvoicesActionCreator
  });
}

const BillStep1Container = connect(
  mapStateToProps,
  mapDispatchToProps
)(BillStep1Screen);

export default BillStep1Container;


