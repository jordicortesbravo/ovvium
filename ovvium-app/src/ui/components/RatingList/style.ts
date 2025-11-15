import { ViewStyle, TextStyle, StyleSheet } from 'react-native';
import { AppColors } from '../../styles/layout/AppColors';
import { AppFonts } from '../../styles/layout/AppFonts';

interface RatingItemStyle {
    container: ViewStyle;
    user: TextStyle;
    lastUpdate: TextStyle;
    stars: ViewStyle;
    comment: TextStyle;
  }
  
  export const ratingItemStyles = StyleSheet.create<RatingItemStyle>({
    container: {
      backgroundColor: AppColors.ratingBackground,
      borderRadius: 8,
      paddingVertical: 10,
      paddingHorizontal:20,
      marginHorizontal: 15,
      marginBottom: 10
    },
    user: {
      fontSize: 14,
      marginBottom:7,
      fontFamily: AppFonts.medium
    },
    lastUpdate: {
        fontSize: 12,
        color:AppColors.listItemDescriptionText,
        position:'absolute',
        right: 20,
        top: 10,
        fontFamily: AppFonts.regular
    },
    stars: {
        marginVertical: 12
    },
    comment: {
      marginTop: 11,
      fontFamily: AppFonts.regular
    }
  });