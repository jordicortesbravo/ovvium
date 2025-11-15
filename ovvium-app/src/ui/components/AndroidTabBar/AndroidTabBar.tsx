import React from "react";
import { View } from 'react-native';
import { androidTabBarStyles } from './style';
import { AndroidTab } from '../AndroidTab/AndroidTab';
import { msg } from '../../../services/LocalizationService';

interface AndroidTabBarState {
    selectedTabIndex: number
}

interface AndroidTabBarProps {
    onChange: (text: string) => void;  
}

export class AndroidTabBar extends React.Component<AndroidTabBarProps, AndroidTabBarState> {

    constructor(props: AndroidTabBarProps) {
        super(props);
        this.state = {selectedTabIndex: 0};
    }

    render() {
        return (
            <View style={androidTabBarStyles.container}>
                <AndroidTab title={msg("products:type:drink")} selected={this.state.selectedTabIndex == 0} onPress={(title: string) => {this.onTabClicked(0, title)}}/>
                <AndroidTab title={msg("products:type:food")} selected={this.state.selectedTabIndex == 1} onPress={(title: string) => {this.onTabClicked(1, title)}} />
                <AndroidTab title={msg("products:type:group")} selected={this.state.selectedTabIndex == 2} onPress={(title: string) => {this.onTabClicked(2, title)}} />
            </View>
        )
    }

    onTabClicked(selectedTabIndex: number, title: string) {
        this.setState({selectedTabIndex: selectedTabIndex});
        this.props.onChange(title);
    }
}