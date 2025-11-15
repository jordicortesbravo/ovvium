import { NavigationProp } from "@react-navigation/core";
import React from "react";
import { View } from 'react-native';
import ImagePicker, { ImagePickerOptions } from 'react-native-image-picker';
import { connect } from 'react-redux';
import { AnyAction, Dispatch } from 'redux';
import { addOrderToCartActionCreator, addToCartActionCreator } from "../../../../actions/CartActions";
import { loadFullProduct, loadTotalRatingsCreator, pageRatingsCreator, uploadPictureCreator } from '../../../../actions/ProductActions';
import { Bill } from '../../../../model/Bill';
import { Customer } from '../../../../model/Customer';
import { Allergen } from "../../../../model/enum/Allergen";
import { Crop } from '../../../../model/enum/Crop';
import { ProductType } from "../../../../model/enum/ProductType";
import { ServiceBuilderLocation } from "../../../../model/enum/ServiceBuilderLocation";
import { Order } from "../../../../model/Order";
import { Picture } from '../../../../model/Picture';
import { Product } from '../../../../model/Product';
import { ProductGroup } from "../../../../model/ProductGroup";
import { RatingsPageResponse } from "../../../../model/response/RatingsPageResponse";
import { TotalRating } from "../../../../model/TotalRating";
import { User } from '../../../../model/User';
import { AppState } from '../../../../store/State';
import { CrashlyticsUtil } from "../../../../util/CrashLyticsUtil";
import { headerStyles } from '../../../components/Header/style';
import { ProductDetailView } from '../../../components/ProductDetailView/ProductDetailView';
import { ProductGroupDetailView } from "../../../components/ProductGroupDetailView/ProductGroupDetailView";
import { AppScreens } from '../../../navigation/AppScreens';
import { baseMapDispatchToProps, baseMapStateToProps, BaseScreen, BaseScreenProps, BaseScreenState } from '../BaseScreen';
import { loadUserRatingsCreator } from './../../../../actions/ProductActions';
import { ArrayUtils } from "../../../../util/ArrayUtils";
import { UserRating } from "../../../../model/UserRating";
import { RatingResponse } from "../../../../model/response/RatingResponse";



interface ProductDetailScreenProps extends BaseScreenProps {
  product: Product;
  customer: Customer;
  bill: Bill;
  me: User;
  userAllergens: Allergen[];
  navigation: NavigationProp<any>;
  addToCart: (product: Product, user: User) => void;
  addOrderToCart: (order: Order) => void;
  loadFullProduct: (customer: Customer, product: Product) => Promise<Product|ProductGroup>;
  loadTotalRatings: (product: Product) => Promise<TotalRating[]>;
  loadUserRatings: (product: Product, user: User) => Promise<RatingResponse>;
  loadRatings: (product: Product, page: number) => Promise<RatingsPageResponse>;
  uploadPhoto: (customer: Customer, product: Product, image: any) => Promise<any>;
}

interface ProductDetailScreenState extends BaseScreenState {
  product: Product|ProductGroup;
  totalRatings?: TotalRating[];
  ratings?: RatingsPageResponse;
  refreshRatings?: boolean;
  uploadingImage: boolean;
}

class ProductsDetailScreen extends BaseScreen<ProductDetailScreenProps, ProductDetailScreenState> {

  constructor(props: ProductDetailScreenProps) {
    super(props, {product: props.product, uploadingImage: false});
  }

  static navigationOptions = {
    header: (
      <View style={headerStyles.emptyHeaderContainer}/>
    )
  };

  componentDidMount() {
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
    }
  }

  render() {
    //Cuando se selecciona el botón pedir dentro de un producto que compone un menú, este valor estará informado
    var selectedProductInsideGroup = this.props.route.params ? this.props.route.params['selectedProductInsideGroup'] : undefined;
    if(this.state.product.type == ProductType.GROUP) {
      return  <ProductGroupDetailView
                product={this.state.product as ProductGroup} 
                goBack={this.goBack.bind(this)} 
                goToProductDetail={(product) => {
                  this.props.navigation.navigate(AppScreens.ProductDetailInsideGroup, {product});
                }}
                selectedProductInsideGroup={selectedProductInsideGroup}
                onAddOrderToCart={(order) => {
                  if(order) {
                    this.onAddOrderToCart(order);
                  }
                }}
                userAllergens={this.props.userAllergens}
              />
    } else {
        return  <ProductDetailView 
                  product={this.state.product} 
                  uploadingImage={this.state.uploadingImage}
                  ratings={this.getRatings().ratings}
                  totalRatings={this.getRatings().totalRatings}
                  goBack={this.goBack.bind(this)} 
                  goToRateView={() => {
                    this.props.loadUserRatings(this.state.product, this.props.me).then( rr => {
                      this.props.navigation.navigate(AppScreens.RateProduct)
                    });
                  }}
                  onAddToCart={this.onAddToCart.bind(this)}
                  onUploadPhoto={this.onUploadPhoto.bind(this)}
                />
    }
  }
  
  getRatings() {
    var userRating:RatingResponse|undefined;
    if(this.props.route.params && this.props.route.params['userRating']) {
      userRating = this.props.route.params['userRating']
    }
    var ratings;
    if(userRating) {
      if(this.state.ratings && this.state.ratings.ratings.length > 0) {
          if(ArrayUtils.contains(this.state.ratings.ratings, userRating, "id")) {
            var ratingsList = ArrayUtils.replace(this.state.ratings.ratings, userRating, "id");
            ratings = this.state.ratings;
            ratings.ratings = ratingsList;
          } else {
            ratingsList = [userRating];
            this.state.ratings.ratings.forEach(r => ratingsList.push(r));
            ratings = {
              pageOffset: this.state.ratings.pageOffset,
              totalPages: this.state.ratings.totalPages,
              numberOfElements: this.state.ratings.numberOfElements + 1,
              ratings: ratingsList
            }
          }
      } else {
          ratings = {
            pageOffset: 0,
            totalPages: 1,
            numberOfElements: 1,
            ratings: [userRating]
          } as RatingsPageResponse;
          var totalRating = 
            {
              productId: this.props.product.id,
              rating: userRating.rating,
              total: 1,
              percentage: 100
            }
          ;
          var totalRatings = new Array<TotalRating>();
          [5,4,3,2,1].forEach(index => {
            if(index == userRating!.rating) {
              totalRatings[index-1] = totalRating
            } else {
              totalRatings[index-1] = {
                productId: this.props.product.id,
                rating: index,
                total: 0,
                percentage: 0
              }
            }
          })
          return {ratings, totalRatings}
      }
    } else {
      return {ratings: this.state.ratings, totalRatings: this.state.totalRatings};
    }
    return {ratings, totalRatings: this.state.totalRatings};
  }

  goBack() {
    this.props.navigation.goBack();
  }

  onAddToCart() {
    if(this.state.product.serviceBuilderLocation == ServiceBuilderLocation.BAR) {
      this.props.addToCart(this.state.product, this.props.me);
      this.goBack();
    } else {
      this.props.navigation.navigate(AppScreens.ConfigureProduct, {product: this.state.product});
    }
  }

  onAddOrderToCart(order: Order) {
    order.user = this.props.me;
    this.props.addOrderToCart(order);
    this.props.navigation.navigate(AppScreens.Products);
  }

  async onUploadPhoto() {
    try {
      var options = {mediaType: 'photo'} as ImagePickerOptions;
      
      ImagePicker.launchImageLibrary(options, (response: any) => {
          if(!response.didCancel) {
            this.props.uploadPhoto(this.props.customer, this.state.product, {uri: response.uri, type:response.type, name:response.fileName}).then((response) => {
              this.props.loadFullProduct(this.props.customer, this.state.product).then(product => {
                this.setState({product: product, uploadingImage:false});
              });
            } );
          } else {
            this.setState({uploadingImage: false});
          }
      });
      this.setState({uploadingImage: true});
    } catch(error) {
      CrashlyticsUtil.recordError("Error in uploadProductPhoto", error);
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

function mapStateToProps(state: AppState): ProductDetailScreenProps {
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
    addOrderToCart: addOrderToCartActionCreator,
    loadFullProduct: loadFullProduct,
    loadTotalRatings: loadTotalRatingsCreator,
    loadUserRatings: loadUserRatingsCreator,
    loadRatings: pageRatingsCreator,
    uploadPhoto: uploadPictureCreator,
  });
}

const ProductDetailContainer = connect(mapStateToProps, mapDispatchToProps)(ProductsDetailScreen);

export { ProductDetailContainer };

