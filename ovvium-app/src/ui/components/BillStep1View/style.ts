import { StyleSheet, ViewStyle, TextStyle } from "react-native";
import { AppColors } from '../../styles/layout/AppColors';
import { AppFonts } from "../../styles/layout/AppFonts";

interface BillStep1ViewStyle {
  container: ViewStyle;
  totalPriceContainer: ViewStyle;
  totalPriceTextLeft: TextStyle;
  totalPriceTextRight: TextStyle;
}

export const billStep1ViewStyle = StyleSheet.create<BillStep1ViewStyle>({
  container: {
    flex: 1,
    backgroundColor: AppColors.white
  },
  totalPriceContainer:{
    flexDirection:'row', 
    marginTop:3
  },
  totalPriceTextLeft: {
    fontSize: 18, 
    marginLeft: 80, 
    fontWeight:'500',
    color: AppColors.mainText,
    fontFamily:  AppFonts.regular
  },
  totalPriceTextRight: {
    fontSize: 18, 
    right:25, 
    position:'absolute',
    fontFamily:  AppFonts.bold,
    color: AppColors.mainText
  }
});
