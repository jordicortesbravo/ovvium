
import { StyleSheet, TextStyle, ViewStyle } from "react-native";
import { AppColors } from '../../styles/layout/AppColors';

interface ProfileViewStyle {
  container: ViewStyle;
  headerContainer: ViewStyle;
  headerText: TextStyle;
}

export const profileViewStyle = StyleSheet.create<ProfileViewStyle>({
  container: {
    backgroundColor: AppColors.white
  },
  headerContainer: {
    height: 60,
    backgroundColor: AppColors.background,
    borderBottomColor: AppColors.separator,
    borderBottomWidth: 0.5,
    elevation: 2,
    flexDirection: 'row'
  },
  headerText: {
    fontSize: 20,
    paddingTop: 15,
    paddingLeft: 20,
    color: AppColors.mainText
  }
});
