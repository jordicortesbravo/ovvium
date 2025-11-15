import { StyleSheet, ViewStyle } from "react-native";
import { AppColors } from '../../styles/layout/AppColors';

interface LoadingViewStyle {
  container: ViewStyle;
}

export const loadingViewStyles = StyleSheet.create<LoadingViewStyle>({
  container: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: AppColors.white,
  }
});