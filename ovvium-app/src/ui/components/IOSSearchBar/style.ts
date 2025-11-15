import { StyleSheet, ViewStyle, TextStyle } from "react-native";
import { AppColors } from '../../styles/layout/AppColors';
import { AppFonts } from "../../styles/layout/AppFonts";

interface IOSSearchBarStyles {
  container: ViewStyle;
  searchIcon: ViewStyle;
  clearIcon: ViewStyle;
  inputTouchWrapper: ViewStyle;
  inputWrapper: ViewStyle;
  input: TextStyle;
  cancelText: TextStyle;
}

export const iosSearchBarStyles = StyleSheet.create<IOSSearchBarStyles>({
    container: {
      paddingTop: 4,
      paddingBottom: 10,
      paddingHorizontal: 12,
      flexDirection: 'row',
      alignItems: 'center',
    },
    searchIcon: {
      paddingLeft: 9,
      backgroundColor: 'transparent',
      marginTop: 2,
    },
    clearIcon: {
      paddingRight: 9,
      backgroundColor: 'transparent',
      marginTop: 2,
    },
    inputTouchWrapper: {
      flexGrow: 1,
      borderRadius: 10,
    },
    inputWrapper: {
      flexDirection: 'row',
      paddingBottom: 5,
      paddingTop: 3,
      borderRadius: 10,
      flexGrow: 1,
      height: 36,
      alignItems: 'center',
      overflow: 'hidden',
      backgroundColor: '#EAEBED',
    },
    input: {
      backgroundColor: 'transparent',
      fontFamily: AppFonts.regular,
      paddingHorizontal: 7,
      fontSize: 16,
      flexGrow: 1,
      color: 'black',
    },
    cancelText: {
      paddingHorizontal: 7,
      color: AppColors.main
    }
  });