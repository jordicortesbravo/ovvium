import React from "react";
import { Image, Text, TouchableHighlight, View, ViewStyle } from 'react-native';
import { User } from '../../../model/User';
import { StringUtils } from '../../../util/StringUtils';
import { AppColors } from '../../styles/layout/AppColors';
import MultifamilyIcon, { IconFamily } from '../MultiFamilyIcon/MultifamilyIcon';
import { userImageProfileViewStyle } from './style';

interface UserImageProfileProps {
  user: User;
  showName?: boolean;
  touchable?: boolean;
  selected?: boolean;
  onPress?: () => void;
}

interface UserImageProfileState {
    showName: boolean;
    touchable: boolean;
    selected: boolean;
}


export class UserImageProfile extends React.Component<UserImageProfileProps, UserImageProfileState> {

    constructor(props: UserImageProfileProps) {
        super(props);
        this.state = {
            showName: props.showName !== undefined ? props.showName : false,
            touchable: props.touchable !== undefined ? props.touchable : false,
            selected: props.selected !== undefined ? props.selected : false,
        }
    }

    UNSAFE_componentWillReceiveProps(props: UserImageProfileProps) {
        this.setState({selected: props.selected !== undefined ? props.selected : false});
    }

    render() {
        if(this.state.touchable) {
            return  <TouchableHighlight underlayColor={AppColors.white} style={[userImageProfileViewStyle.memberIconContainer, this.state.showName ? {marginHorizontal:5} : {}]} key={this.props.user.id} 
                            onPress={() => {
                                this.setState({selected: !this.state.selected});
                                    var props = this.props;
                                    setTimeout(() => {
                                        if(props.onPress) {
                                            props.onPress();
                                        }
                                    }, 1);
                                }
                            }>
                       {this.props.user.imageUri ? this.renderUserImage() : this.renderPlaceholder()}
                    </TouchableHighlight>
        } else {
            return  <View style={[userImageProfileViewStyle.memberIconContainer, this.state.showName ? {marginHorizontal:5} : {}]} key={this.props.user.id} >
                        {this.props.user.imageUri ? this.renderUserImage() : this.renderPlaceholder()}
                    </View>
        }
    }

    selectedMemberStyle(): ViewStyle {
        return {
            borderColor: this.state.selected ? AppColors.funnyGreen : AppColors.white,
            borderWidth: 1,
            backgroundColor: 'white',
            borderRadius: 27,
            height: 52,
            width: 52,
            justifyContent:'center', 
            alignItems:'center'
        }
    }

    renderPlaceholder() {
        var size = AppColors.userPlaceholderColors.length-1;
        var colorId = StringUtils.uuidToInt(this.props.user.id)
        var colors = AppColors.userPlaceholderColors[colorId%size]
        return  <View style={{marginHorizontal: 2}}>
                    <View style={[this.selectedMemberStyle()]}>
                        <View style={{backgroundColor: colors.soft,  borderRadius: 22.5,height: 45,width: 45, justifyContent:'center', alignItems:'center'}}>
                            <MultifamilyIcon family={IconFamily.FEATHER} name="user" size={30} style={[userImageProfileViewStyle.memberIcon, {color: colors.hard}]} />
                        </View>
                    </View>
                    {this.state.showName && <Text style={[userImageProfileViewStyle.memberIconName, {color: AppColors.mainText}]}>{this.getName()}</Text>}
                    {this.state.selected && this.renderSelectedIcon()}
                </View>
    }

    renderUserImage() {
        return  <View style={{marginHorizontal: 2}}>
                    <View style={this.selectedMemberStyle()}>
                        <Image source={{ uri: this.props.user.imageUri}} style={[userImageProfileViewStyle.memberImage, ]} />
                    </View>
                    {this.state.showName && <Text style={[userImageProfileViewStyle.memberIconName, {color: AppColors.mainText}]}>{this.getName()}</Text>}
                    {this.state.selected && this.renderSelectedIcon()}
                </View>
    }

    renderSelectedIcon() {
        return  <View style={{
                    position:'absolute', 
                    bottom:14, 
                    left:33,
                    width: 20,
                    height: 20,
                    justifyContent: 'center',
                    alignItems: 'center',
                    alignContent: 'center',
                    borderRadius: 10,
                    backgroundColor: AppColors.white,
                    zIndex: 2}}>
                    <MultifamilyIcon family={IconFamily.ION} name="ios-checkmark-circle" size={20} color={AppColors.funnyGreen} />
                </View>
    }

    getColor() {
        return this.state.selected ? AppColors.ovviumYellow : AppColors.configHeaderText;
        //return this.state.selected ? AppColors.main : AppColors.configHeaderText;
    }

    getName() {
        return StringUtils.abbreviate(this.props.user.name, 12);
    }
}
