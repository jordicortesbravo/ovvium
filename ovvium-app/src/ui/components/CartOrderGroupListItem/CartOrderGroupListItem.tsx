import React from 'react';
import { Text, View, TextStyle, ScrollView, Dimensions, NativeSyntheticEvent, NativeScrollEvent, Animated } from 'react-native';
import { cartOrderGroupListItemStyle } from './style';
import { Order } from '../../../model/Order';
import { msg } from '../../../services/LocalizationService';
import { getTotalAmount, getPendingAmount } from '../../../services/BillService';
import { Product } from '../../../model/Product';
import { AppColors } from '../../styles/layout/AppColors';
import { TouchableOpacity } from 'react-native-gesture-handler';
import MultifamilyIcon, { IconFamily } from '../MultiFamilyIcon/MultifamilyIcon';
import { ArrayUtils } from '../../../util/ArrayUtils';

interface CartOrderGroupListItemProps {
    orders: Array<Order>;
    product: Product;
    onAddProductToCart: (product: Product) => void;
    onRemoveProductFromCart: (product: Product) => void;
}

interface CartOrderGroupListItemState {
  horizontalPivot: Animated.Value;
}

export class CartOrderGroupListItem extends React.Component<CartOrderGroupListItemProps, CartOrderGroupListItemState> {

    scrollView?: ScrollView;

    constructor(props: CartOrderGroupListItemProps) {
      super(props);
      this.state ={horizontalPivot: new Animated.Value(0)}
    }

    render() {
      var width = Dimensions.get("screen").width;
      return  <ScrollView ref={(comp: ScrollView) => {this.scrollView = comp}} horizontal={true} 
                onScroll={this.refreshColor.bind(this)} scrollEventThrottle={10}
                style={{flexDirection:'row'}} pagingEnabled={false} showsHorizontalScrollIndicator={false}>
                  <Animated.View  style={{width: width, backgroundColor: this.state.horizontalPivot.interpolate({
                        inputRange: [0, 120],
                        outputRange: ['rgba(248, 248, 248, 0)', 'rgba(248, 248, 248, 1)']
                    })}} >
                    <View style={cartOrderGroupListItemStyle.card}>
                      <Text style={cartOrderGroupListItemStyle.numberOfItemsText}>{this.props.orders.length + " x "}</Text>
                      <View style={cartOrderGroupListItemStyle.descriptionContainer}>
                        <View style={{ marginBottom: 3 }}>
                            <Text style={cartOrderGroupListItemStyle.titleText}>{this.props.product.name}</Text>
                          </View>
                          <View>
                            <Text style={cartOrderGroupListItemStyle.descriptionText}>
                              {msg("products:unitPrice") + ": " + this.props.product.price.toFixed(2)+'€'}
                            </Text>
                          </View>
                      </View>
                      <View style={{width:'17%',flexDirection: 'column', alignItems:'flex-end'}}>
                          <View style={cartOrderGroupListItemStyle.priceContainer} >
                            <Text style={cartOrderGroupListItemStyle.priceText}>{getTotalAmount(this.props.orders).toFixed(2)+'€'}</Text>
                          </View>
                      </View> 
                    </View>
                  </Animated.View>
                  <TouchableOpacity style={cartOrderGroupListItemStyle.addButtonContainer} onPress={this.onAddProductToCart.bind(this)} >
                      <MultifamilyIcon family={IconFamily.EVIL} name="plus" color={AppColors.white} size={35} />
                  </TouchableOpacity>
                  <TouchableOpacity style={cartOrderGroupListItemStyle.removeButtonContainer} onPress={this.onRemoveProductFromCart.bind(this)} >
                      <MultifamilyIcon family={IconFamily.EVIL} name="trash" color={AppColors.white} size={35} />
                  </TouchableOpacity>
              </ScrollView>
    }

    onRemoveProductFromCart() {
      if(this.scrollView) {
        this.scrollView.scrollTo({x:0});
        this.props.onRemoveProductFromCart(this.props.product);
      }
    }

    onAddProductToCart() {
      if(this.scrollView) {
        this.scrollView.scrollTo({x:0});
        this.props.onAddProductToCart(this.props.product);
      }
    }

    refreshColor(event: NativeSyntheticEvent<NativeScrollEvent>) {
      this.state.horizontalPivot.setOffset(event.nativeEvent.contentOffset.x);
      this.setState({horizontalPivot: this.state.horizontalPivot});
  }
}