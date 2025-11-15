import React from "react";
import { ActivityIndicator, Image, Platform, Text, TextInput, TouchableOpacity, View, Dimensions, ScrollView } from "react-native";
import { AppleButton } from '@invertase/react-native-apple-authentication';
import { msg } from "../../../services/LocalizationService";
import { StringUtils } from '../../../util/StringUtils';
import { errorDialog } from '../../../util/WidgetUtils';
import { AppScreens } from '../../navigation/AppScreens';
import { AppColors } from "../../styles/layout/AppColors";
import { SocialButton, SocialButtonType, ActionType } from '../SocialButton/SocialButton';
import { registerViewStyles } from './style';
import { AppFonts } from "../../styles/layout/AppFonts";
import { KeyboardAwareScrollView } from "react-native-keyboard-aware-scroll-view";

interface RegisterViewState {
	email: string;
	password: string;
	name: string;
}

interface RegisterViewProps {
	onRegister: (email: string, password: string, name: string) => void;
	onFacebookLogin: () => void;
	onGoogleLogin: () => void;
	onAppleLogin: () => void;
	goToLoginView: () => void;
	showIndicator: boolean;
}

const emailRegexp = new RegExp(/^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/);

export class RegisterView extends React.Component<RegisterViewProps, RegisterViewState> {

	constructor(props: RegisterViewProps) {
		super(props);
		this.state = {} as RegisterViewState;
	}

	render() {
		var passwordInput: TextInput;
		var emailInput: TextInput;
		var width = Dimensions.get('screen').width;
		width = width - width * 0.2;
		return (
			<KeyboardAwareScrollView endFillColor={AppColors.white} style={{ backgroundColor: AppColors.white }}>
				<View style={[registerViewStyles.mainContainer]}>
					<View
						style={{
							marginTop: 50,
							marginBottom: 50,
							width: "100%",
							justifyContent: 'center',
							alignItems: 'center'
						}}>
						<Image source={require('../../../../assets/images/icons/logo.png')} style={{ width: 150, height: 50 }} />
					</View>
					<View style={
						{
							justifyContent: 'center',
							alignItems: 'center',
							width: '100%'
						}
					}>
						<TextInput
							style={registerViewStyles.loginInput}
							autoCapitalize="none"
							autoCorrect={false}
							placeholderTextColor={AppColors.ovviumBlue}
							returnKeyType="next"
							selectionColor={AppColors.ovviumYellow}
							underlineColorAndroid="transparent"
							onSubmitEditing={() => emailInput.focus()}
							onChangeText={text => this.setState({ name: text })}
							placeholder={msg("login:name")}
						/>
						<TextInput
							ref={(input: TextInput) => emailInput = input}
							style={registerViewStyles.loginInput}
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
						/>
						<TextInput
							ref={(input: TextInput) => passwordInput = input}
							style={registerViewStyles.loginInput}
							autoCapitalize="none"
							autoCorrect={false}
							placeholderTextColor={AppColors.ovviumBlue}
							returnKeyType="next"
							selectionColor={AppColors.ovviumYellow}
							underlineColorAndroid="transparent"
							onChangeText={text => this.setState({ password: text })}
							secureTextEntry
							placeholder={msg("login:password")}
						/>

						<View style={registerViewStyles.loginButtonContainer}>
							<TouchableOpacity
								style={{ height: "100%", width: "100%", justifyContent: "center", alignItems: "center" }}
								onPress={() => {
									this.register();
								}}>
								{!this.props.showIndicator && (
									<Text
										style={{
											color: AppColors.white,
											fontSize: 16,
											width: "100%",
											textAlign: "center",
											fontFamily: AppFonts.medium
										}}>
										{msg("login:signup")}
									</Text>
								)}
								{this.props.showIndicator && <ActivityIndicator size="small" color={AppColors.main} />}
							</TouchableOpacity>
						</View>
						<View style={registerViewStyles.loginSocialContainer}>
							<Text style={[registerViewStyles.loginSocialText, { color: 'gray', marginBottom: 20 }]}> – {msg("login:social.login")} – </Text>
							{Platform.OS == 'ios' &&
								<AppleButton
									buttonStyle={AppleButton.Style.BLACK}
									buttonType={AppleButton.Type.SIGN_UP}
									cornerRadius={8}
									style={{ width: width, height: 40, marginBottom: 8 }}
									onPress={this.props.onAppleLogin}
								/>
							}
							<SocialButton type={SocialButtonType.GOOGLE} actionType={ActionType.SIGN_UP} onPress={this.props.onGoogleLogin} style={{ width: width }} />
							<SocialButton type={SocialButtonType.FACEBOOK} actionType={ActionType.SIGN_UP} onPress={this.props.onFacebookLogin} style={{ width: width }} />
						</View>
						<TouchableOpacity style={Platform.OS == 'ios' ? { bottom: 60, position: "absolute", alignItems: 'center', padding: 5 } : { alignItems: 'center', padding: 5 }} onPress={this.props.goToLoginView}>
							<Text style={registerViewStyles.loginSocialText}> {msg("login:alreadyRegistered")}</Text>
						</TouchableOpacity>
					</View>
				</View>
			</KeyboardAwareScrollView>
		);
	}

	register() {

		if (StringUtils.isBlank(this.state.email)) {
			errorDialog({
				message: msg("error:validation:blankEmail"),
				screen: AppScreens.Register
			});
		} else if (!emailRegexp.test(this.state.email)) {
			errorDialog({
				message: msg("error:validation:invalidEmail"),
				screen: AppScreens.Register
			});
		} else if (StringUtils.isBlank(this.state.password)) {
			errorDialog({
				message: msg("error:validation:blankPassword"),
				screen: AppScreens.Register
			});
		} else if (this.state.password.length < 8) {
			errorDialog({
				message: msg("error:validation:badPasswordFormat"),
				screen: AppScreens.Register
			});
		} else if (StringUtils.isBlank(this.state.name)) {
			errorDialog({
				message: msg("error:validation:blankName"),
				screen: AppScreens.Register
			});
		} else {
			this.props.onRegister(this.state.email, this.state.password, this.state.name);
		}
	}
}
