import React from "react";
import { Text, TouchableNativeFeedback, View, ViewStyle } from 'react-native';
import { androidTabStyles } from './style';

interface AndroidTabProps {
    title: string;
    selected?: boolean;
    containerStyle?: ViewStyle;
    onPress: (title: string) => void;
}

export class AndroidTab extends React.Component<AndroidTabProps> {

    render() {
       
        return (
            <TouchableNativeFeedback onPress={() => {this.onTabPressed(this.props.title)}}>
                <View style={[this.props.selected ? androidTabStyles.selectedContainer: androidTabStyles.unselectedContainer, this.props.containerStyle]}>
                    <Text style={this.props.selected ? androidTabStyles.selectedText : androidTabStyles.unselectedText}>{this.props.title}</Text>
                </View>
            </TouchableNativeFeedback>
        );
    }

    onTabPressed(title: string) {
        this.props.onPress(title);
    }
}