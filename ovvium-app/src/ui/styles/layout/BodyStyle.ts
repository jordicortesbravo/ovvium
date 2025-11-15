import { StyleSheet, ViewStyle } from "react-native";

interface BodyStyle {
    container: ViewStyle;
  }
  
  export const bodyStyles = StyleSheet.create<BodyStyle>({
    container: {
      backgroundColor: "#FFF",
      height: '100%'
    }
  });

  