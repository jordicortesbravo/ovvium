import SegmentedControl from '@react-native-community/segmented-control';
import React from "react";
import { Animated, Dimensions, Easing, NativeSyntheticEvent, Platform, RefreshControl, Text, View } from 'react-native';
import ActionSheet from 'react-native-actionsheet';
import { Toolbar } from 'react-native-material-ui';
import { Allergen } from "../../../model/enum/Allergen";
import { ProductType, ProductTypeMapper } from '../../../model/enum/ProductType';
import { Product } from "../../../model/Product";
import { msg } from "../../../services/LocalizationService";
import { AppColors } from '../../styles/layout/AppColors';
import { AndroidTabBar } from '../AndroidTabBar/AndroidTabBar';
import { Header } from '../Header/Header';
import { headerStyles } from '../Header/style';
import { IOSSearchBar } from '../IOSSearchBar/IOSSearchBar';
import { ProductList } from '../ProductList/ProductList';
import { androidToolBarStyles } from './style';
import { Tricks } from '../../../model/enum/Tricks';
import Swipeable from '../../containers/widgets/Swipeable';
import { AppFonts } from '../../styles/layout/AppFonts';
import { ProductGroup } from '../../../model/ProductGroup';

export interface ProductListViewProps {
  products: Product[];
  userAllergens?: Allergen[];
  buttons: string[];
  onSelectProduct: (product: Product) => void;
  onAddToCart: (product: Product) => void;
  refreshData: () => void;
}

interface ProductListViewState {
  horizontalPivot: Animated.Value;
  refreshing: boolean;
  filteredText: string;
  showActionSheet: boolean;
}


export class ProductListView extends React.Component<ProductListViewProps, ProductListViewState> {

  actionSheet?: ActionSheet;

  constructor(props: ProductListViewProps) {
    super(props);
    this.state = {
      horizontalPivot: new Animated.Value(-1),
      refreshing: false,
      filteredText: '',
      showActionSheet: false
    }
  }

  static getDerivedStateFromProps(nextProps: ProductListViewProps, previousState: ProductListViewState) {
    return {
      refreshing: false
    }
  }

  render() {
    var width = Dimensions.get('screen').width;
    return (
      <View style={{ height: "100%", backgroundColor: AppColors.white }}>
        {Platform.OS == 'ios' &&
          <View style={{ height: 50, backgroundColor: AppColors.white, zIndex: 10 }} />
        }
        <Animated.ScrollView style={{ height: "100%", marginTop: Platform.OS == 'ios' ? -50 : 0 }} refreshControl={<RefreshControl onRefresh={this._onRefresh} refreshing={this.state.refreshing} />} >
          {Platform.OS == 'ios' ? this.renderHeaderIos() : this.renderAndroidHeader()}
          <View style={{ flexDirection: 'row' }}>
            <Animated.View style={{
              transform: [{
                translateX: this.state.horizontalPivot.interpolate({
                  inputRange: [-1, 0, 1],
                  outputRange: [0, -width, -2 * width]
                })
              }]
            }}>
              <ProductList
                productType={ProductType.DRINK}
                userAllergens={this.props.userAllergens}
                textFilter={this.state.filteredText}
                products={this.props.products}
                onSelectProduct={this.props.onSelectProduct}
                onRefreshProductList={this.props.refreshData}
                onAddToCart={this.props.onAddToCart} />

            </Animated.View>
            <Animated.View style={{
              transform: [{
                translateX: this.state.horizontalPivot.interpolate({
                  inputRange: [-1, 0, 1],
                  outputRange: [width, -width, -2 * width]
                })
              }]
            }}>
              <ProductList
                productType={ProductType.FOOD}
                userAllergens={this.props.userAllergens}
                textFilter={this.state.filteredText}
                products={this.props.products}
                onSelectProduct={this.props.onSelectProduct}
                onRefreshProductList={this.props.refreshData}
                onAddToCart={this.props.onAddToCart} />
            </Animated.View>
            <Animated.View style={{
              transform: [{
                translateX: this.state.horizontalPivot.interpolate({
                  inputRange: [-1, 0, 1],
                  outputRange: [0, width, -2 * width]
                })
              }]
            }}>
              <ProductList
                productType={ProductType.GROUP}
                userAllergens={this.props.userAllergens}
                textFilter={this.state.filteredText}
                products={this.props.products.filter(p => (p as ProductGroup).timeRangeAvailable)}
                onSelectProduct={this.props.onSelectProduct}
                onRefreshProductList={this.props.refreshData}
                onAddToCart={this.props.onAddToCart} />
            </Animated.View>
          </View>
        </Animated.ScrollView>
        {this.renderHelpPanel()}
      </View>
    );
  }

  renderAndroidHeader() {
    return <View style={[headerStyles.bigContainer, { height: 150 }]}>
      <Toolbar style={androidToolBarStyles}
        leftElement={
          <Header title={msg("products:label")} format="big" subtitle={msg("products:subtitle")} />
        }
        searchable={{
          onChangeText: (text: string) => this.onChangeTextFilter(text),
          onSearchCloseRequested: () => this.onChangeTextFilter(''),
          autoFocus: true,
          placeholder: msg("products:actions:search")
        }}
      />
      <AndroidTabBar onChange={(text: string) => this.onChangeProductType(text)} />
    </View>
  }

  renderHeaderIos() {
    return <View>
      <View style={{ marginTop: 60, zIndex: 5 }}>
        <IOSSearchBar placeholder={msg("products:actions:search")}
          cancelText={msg("actions:cancel")} onChangeText={this.onChangeTextFilter.bind(this)} />
      </View>
      <View style={{ marginTop: -70 }}>
        <Header title={msg("products:label")} format="big" subtitle={msg("products:subtitle")} />
      </View>
      <View style={{ alignItems: 'center' }}>
        {//@ts-ignore
        }
        <SegmentedControl values={this.props.buttons} style={{ width: '80%' }} fontStyle={{ fontFamily: AppFonts.regular }} selectedIndex={0} onChange={(event: NativeSyntheticEvent<any>) => {
          var productType = event.nativeEvent.value.toString();
          if (ProductTypeMapper.of(productType) == ProductType.DRINK) {
            this.onPressDrinkButton();
          } else if (ProductTypeMapper.of(productType) == ProductType.FOOD) {
            this.onPressFoodButton();
          } else {
            this.onPressGroupButton();
          }
        }} />
      </View>

    </View>

  }

  renderHelpPanel() {
    return <Swipeable id={Tricks.PRODUCT_LIST} message={msg("onboarding:tricks:productList")} />
  }
  _onRefresh = () => {
    this.setState({ refreshing: true });
    this.props.refreshData();
  }

  onPressDrinkButton() {
    this.changeHorizontalPivot(-1);
  }

  onPressFoodButton() {
    this.changeHorizontalPivot(0);
  }

  onPressGroupButton() {
    this.changeHorizontalPivot(1);
  }

  changeHorizontalPivot(value: number) {
    Animated.timing(this.state.horizontalPivot, {
      toValue: value,
      easing: Easing.linear,
      duration: 300,
      useNativeDriver: true
    }).start();
  }

  onChangeProductType(productType: string) {
    if (ProductTypeMapper.of(productType) == ProductType.DRINK) {
      this.onPressDrinkButton();
    } else if (ProductTypeMapper.of(productType) == ProductType.FOOD) {
      this.onPressFoodButton();
    } else {
      this.onPressGroupButton();
    }
  }

  onChangeTextFilter(filteredText: string) {
    this.setState({ filteredText });
  }
}

export class ProductListViewHeader extends React.Component<any> {
  render() {
    if (Platform.OS === "ios") {
      return <View style={headerStyles.container}>
        <Text style={headerStyles.text}>{msg("products:label")}</Text>
      </View>
    }
    return <View style={headerStyles.container}>
      <Toolbar
        style={androidToolBarStyles}
        centerElement={this.props.title}
        searchable={this.props.searchable}
      />
    </View>
  }
}