import React from "react";
import { Text, TextStyle, View, ViewStyle } from 'react-native';
import { Button } from '../Button/Button';
import { headerStyles } from './style';
import { GoBackIcon } from '../GoBackIcon/GoBackIcon';
import { HeaderRightButton } from '../HeaderRightButton/HeaderRightButton';
import { AppColors } from '../../styles/layout/AppColors';

interface HeaderProps {
    title: string;
    titleStyle?: TextStyle;
    subtitle?: string;
    format?: "normal" | "big";
    goBackTitle?: string;
    goBack?: () => void;
    goBackContainerStyle?: ViewStyle;
    goBackTextStyle?: TextStyle;
    actionTitle?: string;
    actionIcon?: string;
    doAction?: () => void;
    
}

interface HeaderState {
  format: "normal" | "big"; 
}

export class Header extends React.Component<HeaderProps, HeaderState> {

  constructor(props: HeaderProps) {
    super(props);
    this.state = {format: props.format ? props.format: "normal"}
  }

  render() {
    var goBackStyle = this.state.format == "normal" ? {top: 20} : this.props.goBackContainerStyle;
    return  <View style={this.state.format == "normal" ? headerStyles.container : headerStyles.bigContainer}>
                {this.props.goBack &&
                  <GoBackIcon goBack={this.props.goBack} label={this.props.goBackTitle ? this.props.goBackTitle : undefined} 
                    containerStyle={goBackStyle} textStyle={this.props.goBackTextStyle}/>
                }
                <Text style={[this.state.format == "normal" ? headerStyles.text : headerStyles.bigText, this.props.titleStyle]}>{this.props.title}</Text>
                <Text style={[this.state.format == "normal" ? headerStyles.subtitleHeaderText : headerStyles.bigSubtitleHeaderText]}>{this.props.subtitle}</Text>
                {this.props.doAction && <HeaderRightButton icon={this.props.actionIcon!} onPress={this.props.doAction} color={AppColors.ovviumYellow}/>}
                {this.props.doAction && this.props.actionTitle && !this.props.actionIcon &&
                  <Button label={this.props.actionTitle} onPress={this.props.doAction} onlyText={true} containerStyle={{position:'absolute', right:15, top:25}} textStyle={{color: AppColors.ovviumYellow}}/>
                }
                {this.props.children}
            </View>
  }

}
