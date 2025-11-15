import { ViewStyle, StyleSheet, TextStyle, Platform } from "react-native";
import { AppColors } from '../../styles/layout/AppColors';
import { AppFonts } from "../../styles/layout/AppFonts";

interface IconButtonStyle {
  container: ViewStyle;
  button: ViewStyle;
  text: TextStyle;
}

export const iconButtonStyles = StyleSheet.create<IconButtonStyle>({
  container: {
    alignItems: 'center',
    paddingLeft: 30,
    paddingRight: 30
  },  
  button: {
    borderWidth: 1,
    borderColor: Platform.OS == 'ios' ? AppColors.main : AppColors.white,
    alignItems: "center",
    justifyContent: "center",
    width: 42,
    height: 42,
    backgroundColor: Platform.OS == 'ios' ? AppColors.main : AppColors.white,
    borderRadius: 100
  },
  text: {
    fontSize: 12,
    color: AppColors.main,
    marginTop:5,
    fontFamily: AppFonts.regular
  }
});
