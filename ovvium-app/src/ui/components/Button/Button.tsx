import React from "react";
import { ActivityIndicator, Text, TouchableOpacity, TextStyle, ViewStyle } from "react-native";
import { AppColors } from '../../styles/layout/AppColors';
import { buttonStyles } from './style';

interface ButtonProps {
  label: string;
  textStyle?: TextStyle;
  containerStyle?: ViewStyle;
  showIndicator?: boolean;
  onlyText?: boolean;
  disabled?: boolean;
  onPress: () => void;
}

export class Button extends React.Component<ButtonProps> {
  render() {
    return(
        <TouchableOpacity disabled={this.props.showIndicator || this.props.disabled} style={[this.props.onlyText ? buttonStyles.onlyTextContainer : buttonStyles.container, this.props.containerStyle, this.props.disabled ? buttonStyles.disabled: {}]} onPress={this.props.onPress}>
            {!this.props.showIndicator && (
							 <Text style={[this.props.onlyText ? buttonStyles.onlyTextLabel : buttonStyles.label, this.props.textStyle]}>{this.props.label}</Text>
						)}
						{this.props.showIndicator && <ActivityIndicator size="small" color={AppColors.white} />}
        </TouchableOpacity>
    );
  }
}
