import { StyleSheet, TextStyle, ViewStyle, Platform } from 'react-native';
import { AppColors } from '../../styles/layout/AppColors';
import { AppFonts } from '../../styles/layout/AppFonts';

interface BillResumeItemStyle {
    card: ViewStyle;
    avatarBox: ViewStyle;
    avatar: TextStyle;
    descriptionContainer: ViewStyle;
    titleText: TextStyle;
    descriptionText: TextStyle;
    rightContainer: ViewStyle;
    priceContainer: ViewStyle;
    priceText: TextStyle;
    secondaryPriceContainer: ViewStyle;
    secondaryPriceText: TextStyle;
    arrowContainer: ViewStyle;
    separator: ViewStyle;
}

export const billResumeItemStyle = StyleSheet.create<BillResumeItemStyle>({
    card: {
        height: 90,
        flexDirection: "row",
        justifyContent: 'center',
        alignItems: 'center',
    },
    avatarBox: {
        justifyContent: 'center',
        alignItems: 'center',
        width: 70
    },
    avatar: {
        marginHorizontal:7,
        color: AppColors.secondaryText,
        paddingLeft: 8,
        paddingTop: 4,
        borderRadius: 22.5,
        height: 45,
        width: 45
    },
    descriptionContainer: {
        width:'53%',
      },
    titleText: {
        fontFamily: AppFonts.regular,
        color: AppColors.mainText,
        fontSize: 18,
    },
    descriptionText: {
        fontFamily: AppFonts.regular,
        color: AppColors.listItemDescriptionText,
        fontSize: 12,
        marginTop:4
    },
    rightContainer:{
        width: Platform.OS == 'ios' ? '15%' : '17%',
        flexDirection: 'column', 
    },
    priceContainer: {
        alignItems: 'flex-end'
    },
    priceText: {
        fontFamily: AppFonts.regular,
      color: AppColors.main,
      fontSize:16
    },
    secondaryPriceContainer: {
        paddingTop: 3,
        alignItems: 'flex-end',
    },
    secondaryPriceText: {
        fontFamily: AppFonts.regular,
        color: 'rgba(0,0,0,0.38)',
        fontSize:12
    },
    arrowContainer : {
      justifyContent:'center',
      alignItems: 'center',
      alignContent: 'center',
      paddingLeft: 7
    },
    separator: {
      borderBottomColor: AppColors.separator,
      borderBottomWidth: 1,
      marginTop:3
    }
});
  