import { ViewStyle, StyleSheet, TextStyle } from "react-native";
import { AppColors } from '../../styles/layout/AppColors';
import { AppFonts } from "../../styles/layout/AppFonts";

interface  CircularButtonGroupStyle {
  container: ViewStyle;
  unselectedButton: ViewStyle;
  unselectedText: TextStyle;
  selectedButton: ViewStyle;
  selectedText: TextStyle;
}

export const circularButtonGroupStyle = StyleSheet.create<CircularButtonGroupStyle>({
  container: {
    flexDirection: 'row', 
    alignItems:'center', 
    justifyContent: 'center', 
    marginVertical: 20
  },
  unselectedButton: {
    width: 80, 
    height: 80, 
    borderRadius: 40, 
    borderColor:  AppColors.imagePlaceholderBackground, 
    borderWidth: 1, 
    justifyContent:'center', 
    alignItems: 'center', 
    marginHorizontal:10
  },
  unselectedText: {
    color: AppColors.mainText, 
    fontSize:18,
    fontFamily: AppFonts.medium
  },
  selectedButton: {
    width: 80, 
    height: 80, 
    borderRadius: 40, 
    borderColor:  AppColors.main, 
    backgroundColor: AppColors.main, 
    borderWidth: 1, 
    justifyContent:'center', 
    alignItems: 'center', 
    marginHorizontal:10
  },
  selectedText: {
    color: 'white', 
    fontSize:18,
    fontFamily: AppFonts.bold
  }
});
