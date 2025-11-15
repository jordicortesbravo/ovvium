import React from "react";
import { TouchableOpacity, ViewStyle } from 'react-native';
import { AppColors } from '../../styles/layout/AppColors';
import MultifamilyIcon, { IconFamily } from '../MultiFamilyIcon/MultifamilyIcon';

export interface AndroidFloatingButtonProps {
    style?: ViewStyle;
    iconFamily?: IconFamily;
    iconName: string;
    iconSize?: number;
    iconColor?: string;
    onPress: () => void;
}

export default class AndroidFloatingButton extends React.Component<AndroidFloatingButtonProps> {
    render() {
        var color = this.props.iconColor ? this.props.iconColor : AppColors.white;
        var size = this.props.iconSize ? this.props.iconSize : 24;
        return  <TouchableOpacity onPress={this.props.onPress} style={[{borderRadius: 50, elevation:6, backgroundColor: AppColors.ovviumYellow, height:60, width:60, position:"absolute", bottom:20, right:20, justifyContent:'center', alignItems:'center', zIndex:15}, this.props.style]}>
                    <MultifamilyIcon family={this.props.iconFamily} name={this.props.iconName} color={color} size={size} />
                </TouchableOpacity>
    }
}