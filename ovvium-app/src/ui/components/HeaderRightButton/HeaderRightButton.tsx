import React from "react";
import { TouchableOpacity, View, Platform, ViewStyle } from 'react-native';
import MaterialIcons from "react-native-vector-icons/MaterialIcons";

export interface HeaderRightButtonProps {
    style?: ViewStyle;
    icon: string;
    color?: string;
    onPress: () => void;
}

export class HeaderRightButton extends React.Component<HeaderRightButtonProps> {

    render() {
        return (
           
        <View style={[{position:'absolute', right:Platform.OS == 'ios' ? 10 : 0, paddingTop: Platform.OS == 'ios' ? 40 : 10}, this.props.style]}>
            <TouchableOpacity onPress={this.props.onPress}>
                <MaterialIcons name={this.props.icon} color={this.props.color ? this.props.color : 'black'} size={34} style={{marginRight: 15}} />
            </TouchableOpacity>
        </View>
        )
    }
}