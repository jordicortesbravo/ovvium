
import { ViewStyle, StyleSheet, TextStyle } from "react-native";
import { AppColors } from '../../styles/layout/AppColors';
import { AppFonts } from "../../styles/layout/AppFonts";

interface BillStep5ViewStyle {
  container: ViewStyle;
  titleContainer:ViewStyle;
  titleText: TextStyle;
  dataCardContainer: ViewStyle;
  cardLeftContainer: ViewStyle;
  cardRightContainer: ViewStyle;
  cardDataLeft: TextStyle;
  cardDataRight: TextStyle;
}

export const billStep5ViewStyle = StyleSheet.create<BillStep5ViewStyle>({
  container: {
    flex: 1,
    backgroundColor: AppColors.white,
    height: '100%'
  },
  titleContainer:{
    height:62, 
    //borderBottomColor:AppColors.separator, 
    //borderBottomWidth:0.5
  },
  titleText:{
    fontSize:21, 
    //paddingVertical:20, 
    paddingLeft: 30
  },
  dataCardContainer:{
    minHeight:22, 
    alignItems:'center', 
    //borderBottomColor:AppColors.separator, 
    //borderBottomWidth:0.5, 
    flexDirection:'row',
    marginHorizontal: '5%'
  },
  cardLeftContainer: {
    width:'20%', 
    alignItems:'flex-end'
  },
  cardRightContainer:{
    width:'80%', 
    marginLeft:30
  },
  cardDataLeft:{
    color:AppColors.listItemDescriptionText, 
    fontSize:14,
    fontFamily: AppFonts.regular
  },
  cardDataRight:{
    fontSize:14, 
    fontFamily:AppFonts.regular,
    color: AppColors.mainText
  }
});

