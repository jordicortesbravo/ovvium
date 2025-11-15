import { ImageStyle, StyleSheet, TextStyle, ViewStyle } from "react-native";
import { AppColors } from '../../styles/layout/AppColors';
import { AppFonts } from "../../styles/layout/AppFonts";

interface ProductGroupDetailStyle {
    imageContainer: ImageStyle;
    descriptionContainer: ViewStyle;
    titleContainer: ViewStyle;
    titleText: TextStyle;
    priceText: TextStyle;
    descriptionText: TextStyle;
}

export const productGroupDetailStyles = StyleSheet.create<ProductGroupDetailStyle>({
  imageContainer: {
    width: '100%',
    height: 250
  },
  descriptionContainer : {
    height: 100,
    borderBottomColor: AppColors.separator,
    borderBottomWidth: 0.5,
    marginLeft:10,
    marginRight: 10,
    padding:10
  },
  titleText: {
    fontSize: 18,
    color: AppColors.mainText,
    fontFamily: AppFonts.bold
  },
  titleContainer: {
    flexDirection: 'row',
    width: '100%',
  },
  priceText: {
    fontSize: 18,
    color: AppColors.main,
    fontFamily: AppFonts.regular
  },
  descriptionText: {
    fontSize:14,
    color: AppColors.secondaryText,
    marginTop:10,
    fontFamily: AppFonts.regular
  }
});