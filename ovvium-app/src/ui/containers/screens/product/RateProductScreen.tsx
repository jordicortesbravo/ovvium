import React from "react";
import { View } from 'react-native';
import { connect } from 'react-redux';
import { AnyAction, Dispatch } from 'redux';
import { NavigationProp } from "@react-navigation/core";
import { Customer } from '../../../../model/Customer';
import { Product } from '../../../../model/Product';
import { User } from '../../../../model/User';
import { AppState } from '../../../../store/State';
import { AppScreens } from '../../../navigation/AppScreens';
import { headerStyles } from '../../../components/Header/style';
import { baseMapDispatchToProps, baseMapStateToProps, BaseScreen, BaseScreenProps } from '../BaseScreen';
import { UserRating } from '../../../../model/UserRating';
import { sendProductUserRateCreator } from '../../../../actions/ProductActions';
import { RateProductView } from '../../../components/RateProductView/RateProductView';
import { RatingResponse } from "../../../../model/response/RatingResponse";

interface RateProductScreenProps extends BaseScreenProps {
  product: Product;
  customer: Customer;
  me: User;
  navigation: NavigationProp<any>;
  sendProductUserRate: (product: Product, user: User, userRating: UserRating) => Promise<RatingResponse>;
}

class RateProductScreen extends BaseScreen<RateProductScreenProps, any> {

  static navigationOptions = {
    header: (
      <View style={headerStyles.emptyHeaderContainer}/>
    )
  };

  render() {
    return <RateProductView
              product={this.props.product} 
              goBack={this.goBack.bind(this)} 
              rate={(userRating: UserRating) => this.rate(userRating)}
              openPickPhoto={this.openPickPhoto.bind(this)}
            />
  }

  rate(userRating: UserRating) {
    this.props.sendProductUserRate(this.props.product, this.props.me, userRating).then(ratingResponse => {
      this.props.navigation.navigate(AppScreens.ProductDetail, {userRating: ratingResponse})
    });
  }

  openPickPhoto() {
    this.props.navigation.goBack();
  }

  goBack() {
    this.props.navigation.goBack();
  }

}

function mapStateToProps(state: AppState): RateProductScreenProps {
  return baseMapStateToProps(state, {
    product: state.productsState.selectedProduct,
    customer: state.billState.customer,
    me: state.sessionState.user,
    screen: AppScreens.RateProduct
  })
}

function mapDispatchToProps(dispatch: Dispatch<AnyAction>) {
  return baseMapDispatchToProps(dispatch, {
    sendProductUserRate: sendProductUserRateCreator
  });
}

const RateProductContainer = connect(mapStateToProps, mapDispatchToProps)(RateProductScreen);

export { RateProductContainer };

