import { ImageStyle, StyleSheet, TextStyle, ViewStyle } from "react-native";
import { AppColors } from '../../styles/layout/AppColors';
import { AppFonts } from "../../styles/layout/AppFonts";

interface UserImageProfileViewStyle {
  memberIconContainer: ViewStyle;
  memberIcon: TextStyle;
  memberImage: ImageStyle;
  memberIconName: TextStyle;
}

export const userImageProfileViewStyle = StyleSheet.create<UserImageProfileViewStyle>({
  memberIconContainer: {
    justifyContent:'center',
    alignContent: 'center'
  },
  memberIcon: {
    marginHorizontal: 10,
    paddingLeft: 8,  
    paddingTop: 6,
    borderRadius: 22.5,
    height: 45,
    width: 45
  },
  memberImage: {
    marginHorizontal:10,
    borderRadius: 22.5,
    height: 45,
    width: 45
  },
  memberIconName: {
    color:AppColors.secondaryText, 
    fontSize:10,
    fontWeight: '600',
    textAlign: 'center', 
    marginTop:5,
    fontFamily: AppFonts.regular
  }
});
