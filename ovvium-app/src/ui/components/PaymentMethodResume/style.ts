import { StyleSheet, TextStyle, ViewStyle } from "react-native";
import { AppColors } from '../../styles/layout/AppColors';
import { AppFonts } from "../../styles/layout/AppFonts";

interface PaymentMethodeResumeStyle {
    container: ViewStyle;
    paymentTypeContainer: ViewStyle;
    paymentIdContainer: ViewStyle;
    paymentIdText: TextStyle;
    paymentDetailsContainer: ViewStyle;
    expirationText: TextStyle;
    favouriteText: TextStyle;
}

export const paymentMethodeResumeViewStyles = StyleSheet.create<PaymentMethodeResumeStyle>({
	container: {
        borderWidth:1, 
        borderColor: AppColors.separator, 
        //borderRadius:8, 
        marginHorizontal:10, 
        marginVertical: 10, 
        flexDirection:'row', 
        height:80,
        elevation: 3
    },
    paymentTypeContainer: {
        paddingLeft: 20, 
        marginTop: 25
    },
    paymentIdContainer: {
        paddingLeft: 20, 
        marginTop: 25
    },
    paymentIdText:{
        fontSize:17, 
        fontFamily:AppFonts.medium,
        color: 'black'
    },
    paymentDetailsContainer: {
        position: "absolute", 
        right: 20, 
        marginTop: 25
    },
    expirationText: {
        fontSize:15, 
        color: AppColors.main, 
        textAlign: 'right', 
        fontFamily: AppFonts.medium
    }, 
    favouriteText:{
        color: AppColors.ovviumYellow, 
        fontSize: 12,
        textAlign: 'right', 
        fontFamily: AppFonts.bold,
        marginTop:7
    }
});
