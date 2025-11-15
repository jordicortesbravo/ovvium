import React from "react";
import { Text, TouchableHighlight, View, ViewStyle, TextStyle } from "react-native";
import MaterialIcon from "react-native-vector-icons/MaterialCommunityIcons";
import { socialButtonStyles } from './style';
import { msg } from './../../../services/LocalizationService';
import { LOGIN_SOCIAL_BUTTON_FACEBOOK, LOGIN_SOCIAL_BUTTON_GOOGLE } from "../../../constants/TestIds";
import testID from "../../../util/TestIdsUtils";


export enum SocialButtonType {
  FACEBOOK = "FACEBOOK",
  GOOGLE = "GOOGLE"
}

export enum ActionType {
  SIGN_IN, SIGN_UP
}

interface SocialButtonProps {
  type: SocialButtonType;
  actionType: ActionType;
  style?: ViewStyle;
  onPress: () => void;

}

export class SocialButton extends React.Component<SocialButtonProps> {
  render() {
    return (
      <TouchableHighlight underlayColor="#99d9f4" onPress={this.props.onPress}>
        {this.type()}
      </TouchableHighlight>
    );
  }

  private type() {
    switch (this.props.type) {
      case SocialButtonType.FACEBOOK:
        return this.renderButton(socialButtonStyles.facebookView, socialButtonStyles.facebookText, LOGIN_SOCIAL_BUTTON_FACEBOOK, "facebook", "Facebook");
      case SocialButtonType.GOOGLE:
        return this.renderButton(socialButtonStyles.googleView, socialButtonStyles.googleText, LOGIN_SOCIAL_BUTTON_GOOGLE, "google", "Google");
    }
  }

  private renderButton(viewStyle: ViewStyle, textStyle: TextStyle, testId: string, name: string, msgLabel: string) {
    return (
      <View style={[viewStyle, this.props.style]} {...testID(testId)}>
        <MaterialIcon name={name} size={26} style={textStyle} />
        <Text style={textStyle}>{this.getButtonMsg(msgLabel)}</Text>
      </View>
    );
  }

  private getButtonMsg(socialType: string): React.ReactNode {
    return this.props.actionType == ActionType.SIGN_IN ? msg('login:social:login-button') + ' ' + socialType : msg('login:social:register-button') + ' ' + socialType;
  }
}
