import { ImageStyle, StyleSheet, TextStyle, ViewStyle, Dimensions, Platform } from "react-native";
import { AppColors } from '../../styles/layout/AppColors';
import { AppFonts } from "../../styles/layout/AppFonts";

interface ProductDetailStyle {
    imageContainer: ImageStyle;
    buttonsBarContainer: ViewStyle;
    descriptionContainer: ViewStyle;
    ratingsContainer: ViewStyle;
    titleContainer: ViewStyle;
    titleText: TextStyle;
    priceText: TextStyle;
    descriptionText: TextStyle;
    ratingsTitle: TextStyle;
    ratingsContent: ViewStyle;
    ratingsText: TextStyle;
    ratingNumber: TextStyle;
    ratingsNumberContainer: ViewStyle;
    ratingStarsContainer: ViewStyle;
    allergensContainer: ViewStyle;
}

export const productDetailStyles = StyleSheet.create<ProductDetailStyle>({
  imageContainer: {
    width: '100%',
    height: Dimensions.get('screen').height * 0.45
  },
  buttonsBarContainer: {
    backgroundColor: Platform.OS == 'ios' ? 'rgba(0,0,0,0.02)' : AppColors.white,
    borderBottomColor: AppColors.separator,
    borderBottomWidth: 0.5,
    height: 90,
    paddingTop: 10,
    flex: 1,
    justifyContent: "space-evenly",
    flexDirection: "row",
    alignItems: "flex-start"
  },
  descriptionContainer : {
    height: 100,
    borderBottomColor: AppColors.separator,
    borderBottomWidth: 0.5,
    marginLeft:10,
    marginRight: 10,
    padding:10
  },
  allergensContainer: {
    borderBottomColor: AppColors.separator,
    borderBottomWidth: 0.5,
    marginLeft:10,
    marginRight: 10,
    padding:10,
    marginBottom: 10,
  },
  ratingsContainer : {
    height: 120,
    marginLeft:10,
    marginRight: 10,
    padding:10,
    flexDirection: 'column'
  },
  ratingsTitle : {
    color: AppColors.secondaryText,
    fontFamily: AppFonts.regular
  },
  ratingsContent : {
    flexDirection: 'row',
    justifyContent: 'space-between'
  },
  ratingsNumberContainer : {
    alignItems: 'center',
    justifyContent : 'flex-start'
  },
  ratingStarsContainer : {
    justifyContent: 'flex-end',
    alignItems: 'flex-start',
  },
  titleText: {
    fontSize: 18,
    color: AppColors.mainText,
    fontFamily: AppFonts.bold
  },
  descriptionText: {
    fontSize:14,
    color: AppColors.secondaryText,
    marginTop:10,
    fontFamily: AppFonts.regular
  },
  ratingsText: {
    fontSize: 14,
    color: AppColors.secondaryText,
    fontFamily: AppFonts.regular
  },
  ratingNumber: {
    fontSize: 48,
    color: AppColors.mainText,
    width: 80,
    fontFamily: AppFonts.regular
  },
  titleContainer: {
    flexDirection: 'row',
    width: '100%',
  },
  priceText: {
    fontSize: 18,
    color: AppColors.main,
    fontFamily: AppFonts.regular
  }
});