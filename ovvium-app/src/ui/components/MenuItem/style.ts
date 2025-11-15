import { StyleSheet, TextStyle, ViewStyle } from 'react-native';
import { AppColors } from '../../styles/layout/AppColors';
import { AppFonts } from '../../styles/layout/AppFonts';

interface MenuItemStyle {
    card: ViewStyle;
    avatarBox: ViewStyle;
    avatar: TextStyle;
    descriptionContainer: ViewStyle;
    titleText: TextStyle;
    descriptionText: TextStyle;
    arrowContainer: ViewStyle;
    separator: ViewStyle;
}


export const menuItemStyle = StyleSheet.create<MenuItemStyle>({
    card: {
        height: 90,
        flexDirection: "row",
        marginLeft: 10
    },
    avatarBox: {
        justifyContent: 'center',
        alignItems: 'center',
        alignContent: 'center',
        width: 70
    },
    avatar: {
        borderRadius: 25,
        height: 50,
        width: 50,
        justifyContent: 'center',
        alignItems: 'center',
        alignContent: 'center',
        zIndex:2
    },
    descriptionContainer: {
        justifyContent: 'center',
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
    arrowContainer : {
        position: 'absolute',
        right: 10,
        top: 32.5
    },
    separator: {
      borderBottomColor: AppColors.separator,
      borderBottomWidth: 1,
      marginTop:3
    }
});
  