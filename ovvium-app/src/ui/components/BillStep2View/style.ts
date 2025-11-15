import { StyleSheet, TextStyle, ViewStyle } from "react-native";
import { AppColors } from '../../styles/layout/AppColors';
import { AppFonts } from "../../styles/layout/AppFonts";

interface BillStep2ViewStyle {
  container: ViewStyle;
  membersContainer: ViewStyle;
  memberIconContainer: ViewStyle;
  memberIcon: TextStyle;
  memberIconName: TextStyle;
  totalPriceContainer: ViewStyle;
  totalPriceTextLeft: TextStyle;
  pendingPriceTextLeft: TextStyle;
  totalPriceTextRight: TextStyle;
  totalPendingText: TextStyle;
  payButtonContainer: ViewStyle;
  payButtonText: TextStyle;
  pendingPaymentText: TextStyle;
  tipButtonContainer: ViewStyle;
}

export const billStep2ViewStyle = StyleSheet.create<BillStep2ViewStyle>({
  container: {
    backgroundColor: AppColors.white,
    height: '100%'
  },
  membersContainer: {
    height: 75,
    marginVertical:5,
    paddingHorizontal: 10
  },
  memberIconContainer: {
    justifyContent:'center', 
    marginLeft:20,
  },
  memberIcon: {
    marginLeft: 10,
    color: AppColors.secondaryText,
    paddingLeft: 5,
    paddingTop: 2,
    borderColor: AppColors.secondaryText,
    borderWidth: 1,
    borderRadius: 20,
    height: 40,
    width: 40
  },
  memberIconName: {
    color:AppColors.secondaryText, 
    fontSize:10,
    fontFamily:  AppFonts.medium
  },
  totalPriceContainer:{
    flexDirection:'row', 
    marginTop:3
  },
  totalPriceTextLeft: {
    fontSize: 18, 
    marginLeft: 80, 
    fontWeight:'500',
    color: AppColors.mainText,
    fontFamily:  AppFonts.regular
  },
  pendingPriceTextLeft: {
    color: AppColors.listItemDescriptionText,
    fontSize: 12, 
    marginLeft: 85,
    fontFamily:  AppFonts.regular
  },
  totalPriceTextRight: {
    fontSize: 18, 
    right:25, 
    position:'absolute',
    fontFamily:  AppFonts.bold,
    color: AppColors.mainText
  },
  totalPendingText: {
    color:AppColors.red, 
    position:"absolute", 
    right:30, 
    fontSize:13,
    fontFamily:  AppFonts.medium,
  },
  payButtonContainer: {
  },
  payButtonText: {
    color: AppColors.white,
    fontSize: 15,
  },
  pendingPaymentText: {
    color:AppColors.red, 
    position:"absolute", 
    right:30, 
    fontSize:13
  },
  tipButtonContainer: {
  }
});
