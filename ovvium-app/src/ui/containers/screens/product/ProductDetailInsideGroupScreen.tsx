import { NavigationProp } from "@react-navigation/core";
import React from "react";
import { View } from 'react-native';
import ImagePicker, { ImagePickerOptions } from 'react-native-image-picker';
import { connect } from 'react-redux';
import { AnyAction, Dispatch } from 'redux';
import { addToCartActionCreator, addOrderToCartActionCreator } from "../../../../actions/CartActions";
import { loadTotalRatingsCreator, pageRatingsCreator, uploadPictureCreator, loadFullProduct } from '../../../../actions/ProductActions';
import { Bill } from '../../../../model/Bill';
import { Customer } from '../../../../model/Customer';
import { Crop } from '../../../../model/enum/Crop';
import { Picture } from '../../../../model/Picture';
import { Product } from '../../../../model/Product';
import { User } from '../../../../model/User';
import { AppState } from '../../../../store/State';
import { headerStyles } from '../../../components/Header/style';
import { ProductDetailView } from '../../../components/ProductDetailView/ProductDetailView';
import { AppScreens } from '../../../navigation/AppScreens';
import { baseMapDispatchToProps, baseMapStateToProps, BaseScreen, BaseScreenProps, BaseScreenState } from '../BaseScreen';
import { loadUserRatingsCreator } from './../../../../actions/ProductActions';
import { ServiceBuilderLocation } from "../../../../model/enum/ServiceBuilderLocation";
import { TotalRating } from "../../../../model/TotalRating";
import { Rating } from "../../../../model/Rating";
import { RatingsPageResponse } from "../../../../model/response/RatingsPageResponse";
import { ProductGroup } from "../../../../model/ProductGroup";
import { productDetailStyles } from "../../../components/ProductDetailView/style";
import { ProductType } from "../../../../model/enum/ProductType";
import { ProductGroupDetailView } from "../../../components/ProductGroupDetailView/ProductGroupDetailView";
import { Allergen } from "../../../../model/enum/Allergen";
import { CrashlyticsUtil } from "../../../../util/CrashLyticsUtil";
import { Order } from "../../../../model/Order";
import { CreateOrderRequest } from "../../../../model/request/CreateOrderRequest";
import { RatingResponse } from "../../../../model/response/RatingResponse";



interface ProductDetailInsideGroupScreenProps extends BaseScreenProps {
  customer: Customer;
  me: User;
  userAllergens: Allergen[];
  addToCart: (product: Product, user: User) => void;
  loadFullProduct: (customer: Customer, product: Product) => Promise<Product|ProductGroup>;
  loadTotalRatings: (product: Product) => Promise<TotalRating[]>;
  loadUserRatings: (product: Product, user: User) => Promise<RatingResponse>;
  loadRatings: (product: Product, page: number) => Promise<RatingsPageResponse>;
  uploadPhoto: (customer: Customer, product: Product, image: any) => Promise<any>;
}

interface ProductDetailInsideGroupScreenState extends BaseScreenState {
  product?: Product|ProductGroup;
  totalRatings?: TotalRating[];
  ratings?: RatingsPageResponse;
  uploadingImage: boolean;
}

class ProductDetailInsideGroupScreen extends BaseScreen<ProductDetailInsideGroupScreenProps, ProductDetailInsideGroupScreenState> {

  constructor(props: ProductDetailInsideGroupScreenProps) {
    super(props, {uploadingImage: false});
  }

  static navigationOptions = {
    header: (
      <View style={headerStyles.emptyHeaderContainer}/>
    )
  };

  static getDerivedStateFromProps(nextProps: ProductDetailInsideGroupScreenProps, previousState: ProductDetailInsideGroupScreenState) {
    var product = nextProps.route.params != undefined && nextProps.route.params['product'] != undefined ? nextProps.route.params['product'] : previousState.product;
    return {...previousState, product};
  }

  componentDidMount() {
    if(this.state.product) {
        if(this.state.product.ratingPage == undefined) {
            this.state.product.ratingPage = 0;
          this.props.loadFullProduct(this.props.customer, this.state.product).then(product => {
            this.setState({product: product});
          });
          this.props.loadTotalRatings(this.state.product).then(totalRatings => {
            this.setState({totalRatings});
          });
          this.props.loadRatings(this.state.product, this.state.product.ratingPage).then(ratings => {
            this.setState({ratings});
          });
          this.props.loadUserRatings(this.state.product, this.props.me);
        }
    }
    
  }

  render() {
    return  <ProductDetailView 
                product={this.state.product!} 
                ratings={this.state.ratings}
                totalRatings={this.state.totalRatings}
                goBack={this.goBack.bind(this)} 
                uploadingImage={this.state.uploadingImage}
                goToRateView={() => {
                  this.props.loadUserRatings(this.state.product!, this.props.me).then( rr => {
                    this.props.navigation.navigate(AppScreens.RateProduct)
                  });
                }}
                onAddToCart={() => this.props.navigation.navigate(AppScreens.ProductDetail, {selectedProductInsideGroup: this.state.product})}
                onUploadPhoto={this.onUploadPhoto.bind(this)}
            />
  }
  

  goBack() {
    this.props.navigation.goBack();
  }

  onAddToCart() {
    
  }

  async onUploadPhoto() {
    try {
      var options = {mediaType: 'photo'} as ImagePickerOptions;
      ImagePicker.launchImageLibrary(options, (response: any) => {
          if(!response.didCancel) {
            // this.state.pictures.push(new Picture(response.uri));
            this.props.uploadPhoto(this.props.customer, this.state.product!, {uri: response.uri, type:response.type, name:response.fileName, uploadingImage: false}).then((response) => {
              this.props.loadFullProduct(this.props.customer, this.state.product!).then(product => {
                this.setState({product: product, uploadingImage:false});
              });
            });
          } else {
            this.setState({uploadingImage: false});
          }
      });
      this.setState({uploadingImage: true});
    } catch(error) {
      CrashlyticsUtil.recordError("Error in uploadProductInsideGroupPhoto", error);
    }
  }

  getPictures(product: Product): Picture[] {
    if(product.pictures) {
      return product.pictures.map(p => p[Crop.MEDIUM]);
    } else if(product.coverPicture) {
      
      return [product.coverPicture[Crop.MEDIUM]];
    }
		return [];
	}
}

function mapStateToProps(state: AppState): ProductDetailInsideGroupScreenProps {
  return baseMapStateToProps(state, {
    product: state.productsState.selectedProduct,
    customer: state.billState.customer,
    bill: state.billState.bill,
    me: state.sessionState.user,
    userAllergens: state.profileState.allergens,
    screen: AppScreens.ProductDetail
  })
}

function mapDispatchToProps(dispatch: Dispatch<AnyAction>) {
  return baseMapDispatchToProps(dispatch, {
    addToCart: addToCartActionCreator,
    loadFullProduct: loadFullProduct,
    loadTotalRatings: loadTotalRatingsCreator,
    loadUserRatings: loadUserRatingsCreator,
    loadRatings: pageRatingsCreator,
    uploadPhoto: uploadPictureCreator,
  });
}

const ProductDetailInsideGroupContainer = connect(mapStateToProps, mapDispatchToProps)(ProductDetailInsideGroupScreen);

export { ProductDetailInsideGroupContainer };

