import { ImageStyle, StyleSheet, TextStyle, ViewStyle } from "react-native";
import { AppColors } from '../../styles/layout/AppColors';
import { AppFonts } from "../../styles/layout/AppFonts";

interface CartOrderGroupListItemStyle {
    card: ViewStyle;
    titleText: TextStyle;
    numberOfItemsText: TextStyle;
    descriptionText: TextStyle;
    descriptionContainer: ViewStyle;
    icon: ImageStyle;
    separator: ViewStyle;
    priceContainer: ViewStyle;
    secondaryPriceContainer: ViewStyle;
    priceText: TextStyle;
    secondaryPriceText: TextStyle;
    addButtonContainer: ViewStyle;
    removeButtonContainer: ViewStyle;
  }
export const cartOrderGroupListItemStyle = StyleSheet.create<CartOrderGroupListItemStyle>({
  card: {
    height: 60,
    flexDirection: "row",
    paddingVertical: 4,
    justifyContent: 'center',
    marginLeft: -50
  },
  titleText: {
    color: AppColors.mainText,
    fontSize: 15, 
    paddingLeft: 4,
    fontFamily: AppFonts.regular
  },
  numberOfItemsText: {
    color: AppColors.mainText,
    fontSize: 15,
    fontFamily: AppFonts.regular
  },
  descriptionText: {
    color: AppColors.listItemDescriptionText,
    fontSize: 12,
    paddingTop: -2,
    paddingLeft: 4,
    fontFamily: AppFonts.regular
  },
  descriptionContainer: {
    width: '45%',
  },
  icon: {
    borderRadius: 27.5,
    height: 52,
    width: 52,
    marginLeft:20,
    marginRight:20,
  },
  separator: {
    borderBottomColor: AppColors.lightSeparator,
    borderBottomWidth: 0.5,
    marginLeft:'2%',
    width: "100%"
  },
  priceContainer: {

  },
  secondaryPriceContainer: {
    alignItems: 'flex-end',
    paddingRight: 3
  },
  priceText: {
    color: AppColors.main,
    fontSize:15,
    fontFamily: AppFonts.regular
  },
  secondaryPriceText: {
    fontSize:12,
    paddingTop: 2,
    fontFamily: AppFonts.regular
  },
  addButtonContainer: {
    width: 60, 
    height: 60, 
    zIndex: 2, 
    backgroundColor: AppColors.ovviumYellow, 
    flexDirection: 'row', 
    justifyContent: 'center', 
    alignItems: 'center'
  },
  removeButtonContainer: {
    width: 60, 
    height: 60, 
    zIndex: 2, 
    backgroundColor: AppColors.red, 
    flexDirection: 'row', 
    justifyContent: 'center', 
    alignItems: 'center'
  },
});