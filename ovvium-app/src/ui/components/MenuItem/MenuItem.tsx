import React from 'react';
import { Text, TouchableHighlight, View, ImageStyle } from "react-native";
import MaterialIcon from "react-native-vector-icons/MaterialIcons";
import MultifamilyIcon, { IconFamily } from '../MultiFamilyIcon/MultifamilyIcon';
import { AppColors } from '../../styles/layout/AppColors';
import { menuItemStyle } from './style';
import { AppFonts } from '../../styles/layout/AppFonts';

interface MenuItemProps {
    iconCapitalText?: string;
    iconName?: string;
    iconFamily?: IconFamily;
    iconColor?: string;
    iconTextColor?: string;
    iconStyle?: ImageStyle;
    title: string;
    subtitle?: string;
    hideArrow?: boolean;
    rightElement?: JSX.Element;
    leftElement?: JSX.Element;
    bottomElement?: JSX.Element;

    onPress?: () => void;
}

export class MenuItem extends React.Component<MenuItemProps> {

    render() {
        return (
            <TouchableHighlight underlayColor={AppColors.touchableOpacity} onPress={this.props.onPress}>
                <View>
                    <View style={[menuItemStyle.card, this.props.subtitle ? {} : {height: 50}]}>
                        <View style={[menuItemStyle.avatarBox]}>
                            {(this.props.iconCapitalText || this.props.iconName) && 
                                <View style={[menuItemStyle.avatar, {backgroundColor: this.props.iconColor, paddingLeft:2}]}>
                                    {this.props.iconName && this.props.iconFamily && !this.props.leftElement &&
                                        <MultifamilyIcon family={this.props.iconFamily} name={this.props.iconName} size={25} 
                                            style={[this.props.iconStyle,
                                            {color: this.props.iconTextColor}]}/>
                                    }
                                    {this.props.iconCapitalText &&
                                        <Text style={{fontFamily: AppFonts.bold, fontSize:18, color: this.props.iconTextColor}}>{this.props.iconCapitalText.substr(0,1).toUpperCase()}</Text>
                                    }
                                </View>
                            }
                            {this.props.leftElement}
                        </View>
                        <View style={menuItemStyle.descriptionContainer}>
                            <Text style={[menuItemStyle.titleText, this.props.subtitle ? {} : {marginTop: 17, fontFamily: AppFonts.regular}]}>{this.props.title}</Text>
                            <Text style={menuItemStyle.descriptionText}>{this.props.subtitle}</Text>
                        </View>
                        
                        {this.props.rightElement &&
                            <View style={{position:'absolute', alignItems: 'flex-end', right: 15, top: this.props.subtitle ? 30 : 10, width:'100%'}}>
                                {this.props.rightElement}
                            </View>
                        }
                        {!this.props.hideArrow && 
                            <View style={menuItemStyle.arrowContainer}>
                                <MaterialIcon name="chevron-right" size={22} color='#D1D1D6'/>
                            </View>
                        }
                        {this.props.bottomElement}
                    </View>
                    <View style={{borderBottomColor: 'rgba(0,0,0,0.035)',borderBottomWidth: 1, marginHorizontal:'5%'}}/>
                </View>
            </TouchableHighlight>
        );
    }
}