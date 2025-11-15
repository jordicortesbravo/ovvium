import { ViewStyle } from "react-native";
import { StyleSheet } from "react-native";

interface StarRatingsStyles {
  container: ViewStyle;
}

export const starRatingsStyles = StyleSheet.create<StarRatingsStyles>({
  container: {
    flexDirection: 'row'
    }
});
