import { NavigationProp } from "@react-navigation/core";
import React from "react";
import { View } from 'react-native';
import { connect } from "react-redux";
import { AnyAction, Dispatch } from "redux";
import { addToCartActionCreator } from "../../../../actions/CartActions";
import { listProductsActionCreator, selectProductActionCreator } from "../../../../actions/ProductActions";
import { Bill } from '../../../../model/Bill';
import { Customer } from "../../../../model/Customer";
import { Allergen } from "../../../../model/enum/Allergen";
import { Product } from "../../../../model/Product";
import { User } from "../../../../model/User";
import { msg } from "../../../../services/LocalizationService";
import { AppState } from "../../../../store/State";
import { headerStyles } from '../../../components/Header/style';
import { ProductListView } from '../../../components/ProductListView/ProductListView';
import { AppScreens } from "../../../navigation/AppScreens";
import { baseMapDispatchToProps, baseMapStateToProps, BaseScreen, BaseScreenProps } from '../BaseScreen';
import { ServiceBuilderLocation } from "../../../../model/enum/ServiceBuilderLocation";
import { ProductType } from "../../../../model/enum/ProductType";

interface ProductScreenProps extends BaseScreenProps {
  products: Product[];
  user: User;
  userAllergens?: Allergen[];
  customer: Customer;
  bill: Bill;
  navigation: NavigationProp<any>;
  listProducts: (customer: Customer) => Product[];
  selectProduct: (product: Product) => Product;
  addToCart: (product: Product, user: User) => void;
}

export class ProductListScreen extends BaseScreen<ProductScreenProps, any> {

  static navigationOptions = {
    header: (
      <View style={headerStyles.emptyHeaderContainer} />
    )
  };

  constructor(props: ProductScreenProps) {
    super(props);
  }

  render() {
    const buttons = [msg("products:type:drink"), msg("products:type:food"), msg("products:type:group")];
    return (
      <ProductListView
        refreshData={this.refreshData.bind(this)}
        userAllergens={this.props.userAllergens}
        products={this.props.products}
        buttons={buttons}
        onSelectProduct={this.onSelectProduct.bind(this)}
        onAddToCart={this.onAddToCart.bind(this)}
      />
    );
  }

  onAddToCart(product: Product) {
    if (product.type == ProductType.GROUP) {
      this.props.navigation.navigate(AppScreens.ProductDetail, { product });
    } else if (product.serviceBuilderLocation == ServiceBuilderLocation.BAR) {
      this.props.addToCart(product, this.props.user);
    } else {
      this.props.navigation.navigate(AppScreens.ConfigureProduct, { product });
    }
  }

  UNSAFE_componentWillMount() {
    this.refreshData();
  }

  refreshData() {
    if (this.props.customer) {
      this.props.listProducts(this.props.customer);
    }
  }

  onSelectProduct(product: Product) {
    if (this.props.navigation) {
      this.props.navigation.navigate(AppScreens.ProductDetail);
    }
    this.props.selectProduct(product);
  }
}

function mapStateToProps(state: AppState): ProductScreenProps {
  return baseMapStateToProps(state, {
    products: state.productsState.products.filter(p => !p.hidden),
    user: state.sessionState.user,
    userAllergens: state.profileState.allergens,
    bill: state.billState.bill,
    customer: state.billState.customer,
    screen: AppScreens.Products
  });
}

function mapDispatchToProps(dispatch: Dispatch<AnyAction>) {
  return baseMapDispatchToProps(dispatch,
    {
      listProducts: listProductsActionCreator,
      selectProduct: selectProductActionCreator,
      addToCart: addToCartActionCreator
    });
}

const ProductListContainer = connect(
  mapStateToProps,
  mapDispatchToProps
)(ProductListScreen);

export default ProductListContainer;
