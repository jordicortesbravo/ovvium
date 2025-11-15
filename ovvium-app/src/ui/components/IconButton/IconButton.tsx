import React from "react";
import { Platform, Text, TouchableOpacity, View } from 'react-native';
import { AppColors } from '../../styles/layout/AppColors';
import MultifamilyIcon, { IconFamily } from '../MultiFamilyIcon/MultifamilyIcon';
import { iconButtonStyles } from './style';

interface IconButtonProps {
    title: string;
    icon: string;
    family: IconFamily;
    onPress: () => void;
}

export class IconButton extends React.Component<IconButtonProps> {

    render() {
        return <View  style={iconButtonStyles.container}>
                    <TouchableOpacity style={iconButtonStyles.button} onPress={this.props.onPress}>
                        <MultifamilyIcon name={this.props.icon} family={this.props.family}  size={Platform.OS == 'ios' ? 20 : 25} color={Platform.OS == 'ios' ? AppColors.white : AppColors.main } />
                    </TouchableOpacity>
                    <Text style={iconButtonStyles.text}>{Platform.OS == 'ios' ? this.props.title : this.props.title.toUpperCase()}</Text>
                </View>
                
    }
}
