import React from "react";
import { Dimensions, Image, Platform, ScrollView, Text, TouchableHighlight, TouchableOpacity, View } from "react-native";
import { Allergen } from "../../../model/enum/Allergen";
import { Product } from "../../../model/Product";
import { msg } from '../../../services/LocalizationService';
import { ProductService } from '../../../services/ProductService';
import { StringUtils } from '../../../util/StringUtils';
import { AppColors } from '../../styles/layout/AppColors';
import { AppFonts } from "../../styles/layout/AppFonts";
import MultifamilyIcon, { IconFamily } from '../MultiFamilyIcon/MultifamilyIcon';
import { productListItemStyles } from './style';

interface ProductListItemProps {
  product: Product;
  userAllergens?: Allergen[];
  hidePrice?: boolean;
  horizontalScroll: boolean;
  rightWidget?: Element;
  touchableWidth?: number;
  onSelectProduct: (product: Product) => void;
  onAddToCart?: (product: Product) => void;
}

export class ProductListItem extends React.Component<ProductListItemProps> {
  
  scrollView?: ScrollView;
  
  render() {
    if(this.props.horizontalScroll) {
      return (
          <ScrollView ref={(comp: ScrollView) => this.scrollView = comp} horizontal={true} style={{flexDirection:'row'}} pagingEnabled={false} showsHorizontalScrollIndicator={false}>
            {this.renderBody()}
            <TouchableOpacity style={productListItemStyles.buyButtonContainer} onPress={this.onAddToCart.bind(this)}>
              <MultifamilyIcon family={IconFamily.FEATHER} name="shopping-cart" color={AppColors.white} size={30} />
            </TouchableOpacity>
          </ScrollView>
      );
    } else {
      return  <View style={{flexDirection:'row'}}>
                {this.renderBody()}
              </View>
    }
  }

  renderBody() {
    var width = Dimensions.get("screen").width -140;
    var imageUri = ProductService.getLowProductImageUri(this.props.product);
    var touchableWidth = this.props.touchableWidth ? this.props.touchableWidth : Dimensions.get("screen").width;
    return <View style={{width: Dimensions.get("screen").width}}>
              <TouchableHighlight style={{width: touchableWidth}} underlayColor={AppColors.touchableOpacity} onPress={() => {this.props.onSelectProduct(this.props.product)}}>
                <View>

                <View style={productListItemStyles.card}>
                    <View style={productListItemStyles.imageContainer}>
                      {imageUri && (
                        <Image source={{ uri: imageUri }} style={productListItemStyles.image}  />
                      )}
                    </View>
                    <View style={{marginTop: 0, width:width}}>
                      <Text style={productListItemStyles.title}>{this.props.product.name}</Text>
                      <Text style={productListItemStyles.description}>{this.props.product.description ? StringUtils.abbreviate(this.props.product.description, 30) : this.props.product.category}</Text>
                      <View style={{flexDirection: 'row',position:'absolute', bottom: 0, left: -3}}>
                          <MultifamilyIcon family={IconFamily.FEATHER} name="star" color={AppColors.ovviumYellow} size={16} />
                          <Text style={productListItemStyles.rate}>{this.props.product.rate.toFixed(1)}</Text>
                          <MultifamilyIcon family={IconFamily.FONT_AWESOME} name="comment-o" color={AppColors.ovviumYellow} size={16} />
                          <Text style={productListItemStyles.comments}>{this.props.product.ncomments}</Text>
                          {!this.props.hidePrice && <MultifamilyIcon family={IconFamily.MATERIAL_COMMUNITY} name="currency-eur" color={AppColors.ovviumYellow} size={16} />}
                          {!this.props.hidePrice && <Text style={productListItemStyles.price}>{this.props.product.price.toFixed(2)}</Text>}
                          {this.hasProductSomeUserAllergen() && 
                            <View style={{marginLeft: 10, marginBottom: 3, padding:5, backgroundColor:AppColors.red, borderRadius: 20}}>
                              <Text style={{fontSize: 12, fontFamily: AppFonts.medium, color: AppColors.white}}>{msg("profile:allergens:allergy").toUpperCase()}</Text>
                            </View>
                          }
                      </View>
                    </View>
                      {Platform.OS == 'ios' && !this.props.rightWidget &&
                        <View style={{position:'absolute', right: 10, top: 45}}>
                            <MultifamilyIcon family={IconFamily.EVIL} name="chevron-right" size={22} color='#D1D1D6'/>
                        </View>
                      }
                  </View>
                  <View style={productListItemStyles.separator}/>
                </View>
                </TouchableHighlight>
                {this.props.rightWidget &&
                        <View style={{position:'absolute', right: 20, top: 35}}>
                          {this.props.rightWidget}
                        </View>
                      }
              </View>
        
  }

  onAddToCart() {
    if(this.scrollView && this.props.onAddToCart) {
      this.scrollView.scrollTo({x:0});
      this.props.onAddToCart(this.props.product);
    }
  }

  hasProductSomeUserAllergen() {
    if(this.props.userAllergens == undefined || this.props.product.allergens == undefined || this.props.userAllergens.length == 0 || this.props.product.allergens.length == 0) {
      return false;
    }
    for(var i in this.props.userAllergens) {
      var allergen: Allergen = this.props.userAllergens[i];
      if(this.props.product.allergens.indexOf(allergen) != -1) {
        return true;
      }
    }
    return false;
  }
}