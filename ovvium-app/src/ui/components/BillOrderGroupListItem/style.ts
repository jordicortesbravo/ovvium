import { ImageStyle, StyleSheet, TextStyle, ViewStyle } from "react-native";
import { AppColors } from '../../styles/layout/AppColors';
import { AppFonts } from "../../styles/layout/AppFonts";

interface BillOrderGroupListItemStyle {
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
  }
export const billOrderGroupListItemStyle = StyleSheet.create<BillOrderGroupListItemStyle>({
  card: {
    height: 60,
    flexDirection: "row",
    marginLeft: '5%',
    paddingVertical: 4,
    justifyContent: 'center'
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
    width: '70%',
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
    marginLeft:'7%',
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
  }
});