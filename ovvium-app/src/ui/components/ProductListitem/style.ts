import { ImageStyle, StyleSheet, TextStyle, ViewStyle } from "react-native";
import { AppColors } from '../../styles/layout/AppColors';
import { AppFonts } from "../../styles/layout/AppFonts";

interface ProductListItemStyle {
    card: ViewStyle;    
    imageContainer: ViewStyle;
    image: ImageStyle;
    title: TextStyle;
    description: TextStyle;
    rate: TextStyle;
    comments: TextStyle;
    price: TextStyle;
    separator: ViewStyle;
    buyButtonContainer: ViewStyle;
    buyButtonText: TextStyle;
}


export const productListItemStyles = StyleSheet.create<ProductListItemStyle>({
    card:{
        height: 110, 
        flexDirection: 'row', 
        paddingLeft: 20, 
        paddingVertical: 15 
    },
    imageContainer: {
        height: 80,
        width: 80, 
        marginRight: 30, 
        borderRadius: 10, 
        borderColor: AppColors.gray
    },
    image: {
        borderRadius: 10, 
        height: 80, 
        width: 80
    },
    title: {
        fontFamily: AppFonts.medium,
        fontSize: 17, 
        color: AppColors.mainText
    },
    description: {
        fontFamily: AppFonts.regular,
        fontSize: 12, 
        color: AppColors.listItemDescriptionText, 
        marginTop: 4
    },
    rate: {
        fontFamily: AppFonts.regular,
        marginLeft: 3,
        marginRight: 20, 
        marginTop: 2,
        fontSize:14, 
        color: AppColors.listItemDescriptionText
    },
    comments: {
        fontFamily: AppFonts.regular,
        marginLeft: 3, 
        marginRight: 20, 
        marginTop: 2,
        fontSize:14, 
        color: AppColors.listItemDescriptionText
    },
    price: {
        fontFamily: AppFonts.medium, 
        marginLeft: 3,
        marginTop: 2, 
        fontSize:14, 
        color: AppColors.main
    },
    separator: {
        borderBottomColor: 'rgba(0,0,0,0.035)',
        borderBottomWidth: 1, 
        marginLeft:'5%'
    }, 
    buyButtonContainer: {
        width: 130, 
        zIndex: 2, 
        backgroundColor: AppColors.ovviumYellow, 
        flexDirection: 'row', 
        justifyContent: 'center', 
        alignItems: 'center'
    },
    buyButtonText: {
        fontFamily: AppFonts.regular,
        marginLeft: 10, 
        fontSize:16, 
        color: AppColors.white
    }
});
  