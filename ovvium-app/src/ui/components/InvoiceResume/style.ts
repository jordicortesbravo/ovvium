import { ImageStyle, StyleSheet, TextStyle, ViewStyle } from "react-native";
import { AppColors } from '../../styles/layout/AppColors';
import { AppFonts } from "../../styles/layout/AppFonts";

interface InvoiceResumeStyle {
    card: ViewStyle;
    titleText: TextStyle;
    numberOfItemsText: TextStyle;
    descriptionText: TextStyle;
    descriptionContainer: ViewStyle;
    priceContainer: ViewStyle;
    priceText: TextStyle;
    totalPriceContainer: ViewStyle;
    totalPriceTextLeft: TextStyle;
    totalPriceTextRight: TextStyle;
    title: TextStyle;
    resumeText: TextStyle;
  }
export const invoiceResumeStyle = StyleSheet.create<InvoiceResumeStyle>({
  card: {
    height: 60,
    flexDirection: "row",
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
    width: '63%',
  },
  priceContainer: {

  },
  priceText: {
    color: AppColors.main,
    fontSize:15,
    fontFamily: AppFonts.regular
  },
  totalPriceContainer:{
    flexDirection:'row', 
    marginTop:3
  },
  totalPriceTextLeft: {
    fontSize: 20, 
    marginLeft: 80, 
    fontWeight:'500',
    color: AppColors.mainText,
    fontFamily:  AppFonts.regular
  },
  totalPriceTextRight: {
    fontSize: 20, 
    right:25, 
    position:'absolute',
    fontFamily:  AppFonts.bold,
    color: AppColors.mainText
  },
  title: {
    fontSize:20,
    fontFamily:  AppFonts.bold,
    marginBottom: 10
  },
  resumeText: {
    fontSize:13,
    fontFamily:  AppFonts.regular,
    color: AppColors.listItemDescriptionText
  }
});