import { StyleSheet, ViewStyle } from "react-native";

interface AndroidTabBarStyle {
  container: ViewStyle;
}

export const androidTabBarStyles = StyleSheet.create<AndroidTabBarStyle>({
  container: {
    width: "100%",
    flexDirection: "row",
    height: 48,
    elevation: 2
  }
});
