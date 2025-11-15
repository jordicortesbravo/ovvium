import { NavigationProp, Route } from "@react-navigation/core";
import React from "react";
import { View } from 'react-native';
import { connect } from 'react-redux';
import { AnyAction, Dispatch } from 'redux';
import { addOrderToCartActionCreator } from "../../../../actions/CartActions";
import { Order } from "../../../../model/Order";
import { User } from '../../../../model/User';
import { AppState } from '../../../../store/State';
import { ConfigureProductView } from "../../../components/ConfigureProductView/ConfigureProductView";
import { headerStyles } from '../../../components/Header/style';
import { AppScreens } from "../../../navigation/AppScreens";
import { baseMapDispatchToProps, baseMapStateToProps } from '../BaseScreen';



interface ConfigureProductScreenProps {
  user: User;
  navigation: NavigationProp<any>;
  route: Route<string>;
  addToCart: (order: Order) => void;
}

class ConfigureProductScreen extends React.Component<ConfigureProductScreenProps> {

  static navigationOptions = {
    header: (
      <View style={headerStyles.emptyHeaderContainer}/>
    )
  };

  render() {
    let params = this.props.route.params!;
    let product = params['product'];
    return <ConfigureProductView product={product} goBack={this.props.navigation.goBack} onConfirm={this.onAddToCart.bind(this)} />
  }

  onAddToCart(order: Order) {
    order.user = this.props.user;
    this.props.addToCart(order);
    this.props.navigation.navigate(AppScreens.Products);
  }

}

function mapStateToProps(state: AppState): ConfigureProductScreenProps {
  return baseMapStateToProps(state, {
    product: state.productsState.selectedProduct,
    user: state.sessionState.user
  })
}

function mapDispatchToProps(dispatch: Dispatch<AnyAction>) {
  return baseMapDispatchToProps(dispatch, {
    addToCart: addOrderToCartActionCreator,
  });
}

const ConfigureProductContainer = connect(mapStateToProps, mapDispatchToProps)(ConfigureProductScreen);

export { ConfigureProductContainer };

