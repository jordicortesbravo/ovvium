import { ViewStyle } from "react-native";
import { StyleSheet } from "react-native";

interface StarRatingMultipleStyles {
  rowView: ViewStyle;
  progressBar: ViewStyle;
}

export const starRatingMultipleStyles = StyleSheet.create<StarRatingMultipleStyles>({
  rowView: {
    flexDirection: "row",
    alignSelf: "flex-end"
  },
  progressBar: {
    width: 160,
    height: 4,
    padding: 6,
    marginLeft: 4
  }
});
