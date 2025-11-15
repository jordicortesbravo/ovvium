import { StyleSheet, TextStyle, ViewStyle, Platform } from "react-native";
import { AppColors } from '../../styles/layout/AppColors';
import { AppFonts } from "../../styles/layout/AppFonts";

interface HeaderStyle {
  container: ViewStyle;
  text: TextStyle;
  bigContainer: ViewStyle;
  bigText: TextStyle;
  subtitleHeaderText: TextStyle;
  bigSubtitleHeaderText: TextStyle;
  emptyHeaderContainer: ViewStyle;
}

export const headerStyles = StyleSheet.create<HeaderStyle>({
  container: {
    height: 60,
    backgroundColor: AppColors.background,
    elevation:6,
    marginTop: Platform.OS == 'ios' ? 40 : 0
  },
  text : {
    fontFamily: AppFonts.bold,
    position: "absolute",
    top: 22,
    left: 60,
    fontSize:20,
    color: AppColors.mainText
  },
  bigContainer: {
    height: 100,
    backgroundColor: AppColors.background,
    elevation:0,
    marginBottom: 10,
    marginTop: Platform.OS == 'ios' ? 40 : 0
  },
  bigText: {
    fontFamily: AppFonts.bold,
    position: "absolute",
    top: 35,
    left: 20,
    fontSize:30,
    color: AppColors.mainText
  },
  subtitleHeaderText: {
    fontFamily: AppFonts.regular,
    fontSize:14,
    position: "absolute",
    top: 55,
    left: 70,
    right: 20, 
    color: AppColors.listItemDescriptionText,
  },
  bigSubtitleHeaderText: {
    fontFamily: AppFonts.regular,
    fontSize:14,
    position: "absolute",
    top: 75,
    left: 20,
    right: 20, 
    color: AppColors.listItemDescriptionText,
  },
  emptyHeaderContainer: {
    height: 0
  }
});