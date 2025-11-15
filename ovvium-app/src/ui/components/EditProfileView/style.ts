import { ImageStyle, StyleSheet, TextStyle, ViewStyle, Dimensions, Platform } from "react-native";
import { AppColors } from '../../styles/layout/AppColors';
import { AppFonts } from "../../styles/layout/AppFonts";

interface EditProfileViewStyle {
  imageContainer: ImageStyle;
  searchSection: ViewStyle;
  searchIcon: ViewStyle;
  input: ViewStyle;
}

export const editProfileViewStyles = StyleSheet.create<EditProfileViewStyle>({
  imageContainer: {
    width: '100%',
    height: Dimensions.get('screen').height * 0.35
  },
  searchSection: {
    flex: 1,
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
    width: '50%',
  },
  searchIcon: {
    padding:8
  },
  input: {
    flex: 1,
    padding:4,
    color: '#fff',
    borderBottomColor: AppColors.white,
    borderBottomWidth: 1.2
  }
});