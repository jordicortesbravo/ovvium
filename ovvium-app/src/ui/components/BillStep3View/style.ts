import { ViewStyle, StyleSheet, TextStyle } from "react-native";
import { AppColors } from '../../styles/layout/AppColors';

interface BillStep3ViewStyle {
  container: ViewStyle;
  explanationContainer: ViewStyle;
  innerExplanationContainer: ViewStyle;
  mainText: TextStyle;
  customerText: TextStyle;
  buttonsContainer: ViewStyle;
  buttonText: TextStyle;
}

export const billStep3ViewStyle = StyleSheet.create<BillStep3ViewStyle>({
  container: {
    backgroundColor: AppColors.white,
    height: '100%',
  },
  explanationContainer: {
   
  },
  innerExplanationContainer: {
   
  },
  mainText: {
    fontSize: 17,
    color: AppColors.listItemDescriptionText
  },
  customerText: {
    fontSize: 17,
    color: "black",
  },
  buttonsContainer: {
    flexDirection:'row',
    justifyContent: 'center',
    alignItems: 'center',
    marginHorizontal: 30,
    marginVertical: 10
  },
  buttonText:{
  }
});
