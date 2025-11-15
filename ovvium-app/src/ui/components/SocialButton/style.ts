import { ImageStyle, StyleSheet, TextStyle, ViewStyle } from "react-native";
import { AppFonts } from "../../styles/layout/AppFonts";

interface SocialButtonStyle {
  facebookView: ViewStyle;
  facebookText: TextStyle;
  googleView: ViewStyle;
  googleText: TextStyle;
  imageStyle: ImageStyle;
}

export const socialButtonStyles = StyleSheet.create<SocialButtonStyle>({
  facebookView: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#3b5998',
    justifyContent: 'center',
    height:40,
    elevation: 2,
    borderRadius: 8,
    marginTop: 8,
    marginBottom: 30,
  },
  facebookText: {
    fontSize: 17,
    color: '#FAFAFA',
    marginLeft: 10,
    fontFamily: AppFonts.regular,
  },
  googleView: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#D34836',
    justifyContent: 'center',  
    height:40,
    elevation: 2,
    borderRadius: 8,
    marginVertical: 8
  },
  googleText: {
    fontSize: 17,
    color: '#FAFAFA',
    marginLeft: 10,
    fontFamily: AppFonts.regular
  },
  imageStyle : {
      backgroundColor: '#fff'
  }
});
