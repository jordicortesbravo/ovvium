import { StyleSheet, TextStyle, ViewStyle } from "react-native";
import { AppColors } from '../../styles/layout/AppColors';

interface GoBackIconStyles {
  container: ViewStyle;
  icon: TextStyle;
  label: TextStyle;
}

export const goBackIconStyles = StyleSheet.create<GoBackIconStyles>({
  container: {
    zIndex: 10,
    position: "absolute",
    top: 5,
    left: 15,
    flexDirection: "row"
  },
  icon: {
    color: AppColors.mainText,
  },
  label: {
    color: AppColors.mainText,
    fontSize: 15,
    marginTop: 5,
    marginLeft: 5,
  }
});