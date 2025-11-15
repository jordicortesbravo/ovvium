import { StyleSheet, TextStyle, ViewStyle } from "react-native";
import { AppColors } from '../../styles/layout/AppColors';
import { AppFonts } from "../../styles/layout/AppFonts";

interface AndroidTabStyle {
  selectedContainer: ViewStyle;
  unselectedContainer: ViewStyle;
  selectedText: TextStyle;
  unselectedText: TextStyle;
}

export const androidTabStyles = StyleSheet.create<AndroidTabStyle>({
  selectedContainer: {
    width: "33.3%",
    height: "100%",
    backgroundColor: AppColors.background,
    borderBottomColor: AppColors.main,
    alignItems: "center",
    borderBottomWidth: 2,
  },
  unselectedContainer: {
    width: "33.3%",
    height: "100%",
    backgroundColor: AppColors.background,
    borderBottomColor: AppColors.background,
    alignItems: "center",
    borderBottomWidth: 2
  },
  selectedText: {
    fontFamily: AppFonts.regular,
    color: AppColors.mainText,
    marginTop: 18,
    fontSize: 17
  },
  unselectedText: {
    fontFamily: AppFonts.regular,
    marginTop: 18,
    fontSize: 17
  }
});
