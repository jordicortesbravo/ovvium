import React from "react";
import { ScrollView, View } from 'react-native';
import { msg } from '../../../services/LocalizationService';
import { StringUtils } from "../../../util/StringUtils";
import { errorDialog } from "../../../util/WidgetUtils";
import { AppScreens } from "../../navigation/AppScreens";
import { AppColors } from '../../styles/layout/AppColors';
import { Button } from "../Button/Button";
import { Header } from '../Header/Header';
import { Input } from "../Input/Input";
import { KeyboardAwareScrollView } from 'react-native-keyboard-aware-scroll-view';

interface ChangePasswordViewProps {
    changePassword: (currentPassowrd: string, newPassword: string) => void;
    goBack: () => void;
}

interface ChangePasswordViewState {
    currentPassword?: string;
    newPassword?: string;
    repeateadNewPassword?: string;
    keyboardShown: boolean
}

export class ChangePasswordView extends React.Component<ChangePasswordViewProps, ChangePasswordViewState> {

    constructor(props: ChangePasswordViewProps) {
        super(props);
        this.state = {
            keyboardShown: false
        };
    }

    render() {
        var newPasswordInput: Input;
        var repeatedNewPasswordInput: Input;
        return (<View style={{ backgroundColor: AppColors.white, height: '100%' }}>
            <KeyboardAwareScrollView endFillColor={AppColors.white}
                onKeyboardDidHide={() => this.setState({ keyboardShown: false })}
                onKeyboardDidShow={() => this.setState({ keyboardShown: true })}>
                <Header goBack={this.props.goBack}
                    goBackTitle={msg("actions:back")}
                    title={msg("login:changePassword:title")}
                    format="big"
                    subtitle={msg("login:changePassword:subtitle")} />
                <ScrollView style={{ padding: 10 }}>
                    <Input
                        onSubmitEditing={() => newPasswordInput.focus()}
                        placeholder={msg("login:changePassword:current")}
                        onChangeText={(currentPassword: string) => this.setState({ currentPassword })}
                        containerStyle={{ width: '100%', paddingVertical: 5 }}
                        secureText
                    />
                    <Input
                        ref={(input: Input) => newPasswordInput = input}
                        onSubmitEditing={() => repeatedNewPasswordInput.focus()}
                        placeholder={msg("login:changePassword:new")}
                        onChangeText={(newPassword: string) => this.setState({ newPassword })}
                        containerStyle={{ width: '100%', paddingVertical: 5 }}
                        secureText
                    />
                    <Input
                        ref={(input: Input) => repeatedNewPasswordInput = input}
                        placeholder={msg("login:changePassword:repeatNew")}
                        onChangeText={(repeateadNewPassword: string) => this.setState({ repeateadNewPassword })}
                        containerStyle={{ width: '100%', paddingVertical: 5 }}
                        secureText
                    />
                </ScrollView>
            </KeyboardAwareScrollView>
            {!this.state.keyboardShown && <View style={{ alignItems: 'center', position: 'absolute', bottom: 0, width: '100%', zIndex: 2, height: 60, justifyContent: 'center', backgroundColor: AppColors.white }}>
                <Button label={msg("login:changePassword:title")} onPress={this.changePassword.bind(this)} /></View>}
        </View>
        );
    }

    changePassword() {
        if (StringUtils.isBlank(this.state.currentPassword) ||
            StringUtils.isBlank(this.state.newPassword) ||
            StringUtils.isBlank(this.state.repeateadNewPassword)) {
            errorDialog({
                title: msg("error:validation:changePassword:requiredFields"),
                screen: AppScreens.ChangePassword
            })
        } else if (this.state.newPassword !== this.state.repeateadNewPassword) {
            errorDialog({
                title: msg("error:validation:changePassword:newAndRepeteadPasswordMustBeEqual"),
                screen: AppScreens.ChangePassword
            })
        } else if (this.state.newPassword! == this.state.currentPassword!) {
            errorDialog({
                title: msg("error:validation:changePassword:currentAndNewPasswordCantBeEqual"),
                screen: AppScreens.ChangePassword
            })
        } else if (this.state.newPassword!.length < 8) {
            errorDialog({
                title: msg("error:validation:changePassword:length"),
                screen: AppScreens.ChangePassword
            })
        } else {
            this.props.changePassword(this.state.currentPassword!, this.state.newPassword!);
        }
    }
}
