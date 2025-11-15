import React from "react";
import { ActivityIndicator, Image, Platform, Text, TextInput, TouchableOpacity, View, Dimensions } from "react-native";
import { msg } from "../../../services/LocalizationService";
import { AppColors } from "../../styles/layout/AppColors";
import { SocialButton, SocialButtonType, ActionType } from '../SocialButton/SocialButton';
import { loginViewStyles } from './style';
import { AppleButton } from '@invertase/react-native-apple-authentication';

import * as TestIds from '../../../constants/TestIds';
import testID from "../../../util/TestIdsUtils";
import { AppFonts } from "../../styles/layout/AppFonts";
import { ScrollView } from "react-native-gesture-handler";
import { KeyboardAwareScrollView } from 'react-native-keyboard-aware-scroll-view';

interface LoginViewState {
	email: string;
	password: string;
}

interface LoginViewProps {
	onLogin: (email: string, password: string) => void;
	onFacebookLogin: () => void;
	onGoogleLogin: () => void;
	onAppleLogin: () => void;
	goToRegisterView: () => void;
	onRecoverPassword: () => void;
	showIndicator: boolean;
}

export class LoginView extends React.Component<LoginViewProps, LoginViewState> {
	constructor(props: LoginViewProps) {
		super(props);
		this.state = {} as LoginViewState;
	}

	render() {
		var passwordInput: TextInput;
		var width = Dimensions.get('screen').width;
		width = width - width * 0.2;
		return (
			<KeyboardAwareScrollView endFillColor={AppColors.white} style={{ backgroundColor: AppColors.white }}>
				<View style={loginViewStyles.mainContainer}>
					<View
						style={{
							marginTop: 70,
							marginBottom: 20,
							width: "100%",
							justifyContent: 'center',
							alignItems: 'center',
							backgroundColor: "none"
						}}>
						<Image source={require('../../../../assets/images/icons/logo.png')} style={{ width: 150, height: 50 }} />
					</View>
					<View style={
						{
							justifyContent: 'center',
							alignItems: 'center',
							paddingBottom: 30,
							width: '100%'
						}
					}>
						<TextInput
							style={loginViewStyles.loginInput}
							autoCapitalize="none"
							autoCorrect={false}
							placeholderTextColor={AppColors.ovviumBlue}
							keyboardType="email-address"
							returnKeyType="next"
							selectionColor={AppColors.ovviumYellow}
							underlineColorAndroid="transparent"
							onSubmitEditing={() => passwordInput.focus()}
							onChangeText={text => this.setState({ email: text })}
							placeholder={msg("login:user")}
							{...testID(TestIds.LOGIN_INPUT_USERNAME)}
						/>
						<TextInput
							ref={(input: TextInput) => passwordInput = input}
							style={loginViewStyles.loginInput}
							autoCapitalize="none"
							autoCorrect={false}
							placeholderTextColor={AppColors.ovviumBlue}
							returnKeyType="next"
							selectionColor={AppColors.ovviumYellow}
							underlineColorAndroid="transparent"
							onChangeText={text => this.setState({ password: text })}
							secureTextEntry
							placeholder={msg("login:password")}
							onSubmitEditing={() => this.props.onLogin(this.state.email, this.state.password)}
							{...testID(TestIds.LOGIN_INPUT_PASSWORD)}
						/>
						<TouchableOpacity
							style={loginViewStyles.loginButtonContainer}
							onPress={() => {
								this.props.onLogin(this.state.email, this.state.password);
							}}
							{...testID(TestIds.LOGIN_BUTTON)}
						>
							<View style={{ height: "100%", width: "100%", justifyContent: "center", alignItems: "center" }}>
								{!this.props.showIndicator && (
									<Text
										style={{
											color: AppColors.white,
											fontSize: 16,
											textAlign: "center",
											fontFamily: AppFonts.medium
										}}>
										{msg("login:login")}
									</Text>
								)}
								{this.props.showIndicator && <ActivityIndicator size="small" color={AppColors.main} />}
							</View>
						</TouchableOpacity>


						<View style={[loginViewStyles.loginSocialContainer, { paddingHorizontal: '5%' }]}>
							<Text style={[loginViewStyles.loginSocialText, { color: 'gray', marginBottom: 20 }]}> – {msg("login:social.login")} – </Text>
							{Platform.OS == 'ios' &&
								<AppleButton
									buttonStyle={AppleButton.Style.BLACK}
									buttonType={AppleButton.Type.SIGN_IN}
									cornerRadius={8}
									style={{ width: width, height: 40, marginBottom: 8 }}
									onPress={this.props.onAppleLogin}
									{...testID(TestIds.LOGIN_SOCIAL_BUTTON_APPLE)}
								/>
							}
							<SocialButton type={SocialButtonType.GOOGLE} actionType={ActionType.SIGN_IN} onPress={this.props.onGoogleLogin} style={{ width: width }} />
							<SocialButton type={SocialButtonType.FACEBOOK} actionType={ActionType.SIGN_IN} onPress={this.props.onFacebookLogin} style={{ width: width }} />
							<TouchableOpacity style={{ alignItems: 'center', padding: 5, marginBottom: 20 }} onPress={this.props.onRecoverPassword}>
								<Text style={loginViewStyles.loginSocialText}> {msg("login:recoverPassword:header")}</Text>
							</TouchableOpacity>
							<TouchableOpacity style={Platform.OS == 'ios' ? { alignItems: 'center', padding: 5 } : { alignItems: 'center', padding: 10 }} onPress={this.props.goToRegisterView}>
								<Text style={loginViewStyles.loginSocialText}> {msg("login:register")}</Text>
							</TouchableOpacity>
						</View>
					</View>
				</View >
			</KeyboardAwareScrollView >
		);
	}
}
