import { StyleSheet, TextStyle, ViewStyle } from "react-native";
import { AppColors } from '../../styles/layout/AppColors';
import { AppFonts } from "../../styles/layout/AppFonts";

interface ProductListHeaderStyle {
    container: ViewStyle;
    text: TextStyle;
}


export const productListHeaderStyle = StyleSheet.create<ProductListHeaderStyle>({
  container: {
    backgroundColor: AppColors.white,
    height: 35,
    justifyContent: 'center',
    marginBottom:10
  },
  text: {
    fontFamily: AppFonts.regular,
    color: AppColors.listItemDescriptionText,
    fontSize: 16,
    paddingLeft: 17
  }
});