import { AppColors } from '../../styles/layout/AppColors';
import { ToolbarStyle } from 'react-native-material-ui';
import { AppFonts } from '../../styles/layout/AppFonts';

export const androidToolBarStyles = {
    container: { 
      flex: 1, 
      width: "100%", 
      elevation:0,
      borderWidth:0,
      backgroundColor: AppColors.background 
    },
    titleText: {
      fontFamily: AppFonts.regular
    },
    rightElement: {
      color: AppColors.ovviumYellow
    }
} as ToolbarStyle;