import { StyleSheet, TextStyle, ViewStyle } from "react-native";
import { AppColors } from '../../styles/layout/AppColors';
import { AppFonts } from "../../styles/layout/AppFonts";

interface LoginViewStyle {
	mainContainer: ViewStyle;
	loginInput: TextStyle;
	loginButtonContainer: ViewStyle;
	loginSocialContainer: ViewStyle;
	loginSocialText: TextStyle;
	loginSocialButtons: ViewStyle;
}

export const loginViewStyles = StyleSheet.create<LoginViewStyle>({
	mainContainer: {
        backgroundColor: AppColors.white,
        alignItems: 'center',
        height: '100%',
    },
    loginInput: {
        color: AppColors.ovviumBlue,
        fontSize: 16,
        borderBottomColor: AppColors.ovviumBlue,
        borderBottomWidth: 1.5,
        width: '80%',
        marginBottom:45,
        paddingBottom:10,
        fontFamily: AppFonts.regular
    },
    loginButtonContainer: {
        backgroundColor: AppColors.ovviumYellow,
        width: '80%',
        height: 40,
        elevation: 2,
        borderRadius: 8
    },
    loginSocialContainer: {
		marginVertical: 20,
        alignItems: 'center'
    },
    loginSocialText :  {
        color: AppColors.ovviumBlue,
        fontFamily: AppFonts.regular
    },
    loginSocialButtons: {
		marginVertical: 20,
    }
});
