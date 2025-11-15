import { ImageStyle, StyleSheet, ViewStyle } from "react-native";
import { AppColors } from '../../styles/layout/AppColors';

interface RateProductViewStyle {
  container: ViewStyle;
  rightButtonContainer: ViewStyle;
  image: ImageStyle;
  imageContainer: ViewStyle;
}

export const rateProductViewStyle = StyleSheet.create<RateProductViewStyle>({
  container: {
    flex: 1,
    backgroundColor: AppColors.white
  },
  rightButtonContainer: {
    position: "absolute",
    right: 15,
    paddingTop: 12
  },
  image: {
    width: "100%",
    height: 280
  },
  imageContainer: {
    borderBottomColor: AppColors.separator,
    borderBottomWidth: 0.5,
  }
});
