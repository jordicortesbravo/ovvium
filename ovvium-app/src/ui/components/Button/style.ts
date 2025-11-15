import { StyleSheet, TextStyle, ViewStyle } from "react-native";
import { AppColors } from '../../styles/layout/AppColors';
import { AppFonts } from "../../styles/layout/AppFonts";

interface ButtonStyles {
  container: ViewStyle;
  label: TextStyle;
  onlyTextContainer: ViewStyle;
  onlyTextLabel: TextStyle;
  disabled: ViewStyle;
}

export const buttonStyles = StyleSheet.create<ButtonStyles>({
  container: {
    backgroundColor: AppColors.ovviumYellow,
    height: 45,
    justifyContent: 'center',
    alignItems: 'center',
    borderRadius: 8,
    width:'85%',
    elevation: 2
  },
  label: {
    color: AppColors.white,
    fontSize: 16,
    textAlign: 'center',
    fontFamily: AppFonts.bold
  },
  onlyTextContainer: {
    borderWidth:0, 
    borderColor: AppColors.white, 
    elevation: 0, 
    backgroundColor: AppColors.white
  },
  onlyTextLabel: {
    color: AppColors.secondaryText, 
    fontSize: 16, 
    fontFamily: AppFonts.medium
  },
  disabled: {
    backgroundColor: AppColors.placeholderAndroid
  }
});