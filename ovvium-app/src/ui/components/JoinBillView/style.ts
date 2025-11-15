import { ViewStyle, StyleSheet, TextStyle } from "react-native";
import { AppColors } from '../../styles/layout/AppColors';
import { AppFonts } from "../../styles/layout/AppFonts";

interface JoinBilllStyle {
  headerContainer: ViewStyle;
  bottomContainer: ViewStyle;
  centerContainer: ViewStyle;
  mainText: TextStyle;
  highlightedText: TextStyle;
  camera: ViewStyle;
}

export const joinBillStyle = StyleSheet.create<JoinBilllStyle>({
  headerContainer: {
    height: '100%', 
    paddingHorizontal:25, 
    paddingVertical:20, 
    backgroundColor: AppColors.white
  },
  centerContainer: {
    height: '70%', 
    padding: 25, 
    justifyContent: 'center',
    alignItems: 'center'
  },
  mainText: {
    fontSize: 23,
    color: AppColors.ovviumBlue,
    fontFamily: AppFonts.regular
  },
  highlightedText: {
    color: "black"
  },
  camera: {
    height: 400
  },
  bottomContainer: {
    height: 110, 
    justifyContent: 'center', 
    alignItems: 'center',
    backgroundColor: AppColors.white
  }
});
