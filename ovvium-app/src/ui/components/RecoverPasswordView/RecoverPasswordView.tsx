import React from "react";
import { ActivityIndicator, Image, Text, TextInput, TouchableOpacity, View } from "react-native";
import EvilIcons from "react-native-vector-icons/EvilIcons";
import { msg } from "../../../services/LocalizationService";
import { AppColors } from "../../styles/layout/AppColors";
import { recoverViewStyles } from './style';
import { AppFonts } from "../../styles/layout/AppFonts";

interface RecoverPasswordState {
	email: string;
}

interface RecoverPasswordProps {
	onRecoverPassword: (email:string) => void;
	goToLoginView: () => void;
	showIndicator: boolean;
	passwordRecovered: boolean;
}

export class RecoverPasswordView extends React.Component<RecoverPasswordProps, RecoverPasswordState> {
	constructor(props: RecoverPasswordProps) {
		super(props);
		this.state = {} as RecoverPasswordState;
	}

	render() {
		return (
			<View style={recoverViewStyles.mainContainer}>
				<View
					style={{
						marginTop: 70,
						marginBottom: 50,
						width: "100%",
						justifyContent: 'center',
						alignItems: 'center'
					}}>
					<Image source={require('../../../../assets/images/icons/logo.png')} style={{width:200, height: 75}} />
				</View>

				{!this.props.passwordRecovered && (
					<TextInput
						style={recoverViewStyles.loginInput}
						autoCapitalize="none"
						autoCorrect={false}
						placeholderTextColor={AppColors.ovviumBlue}
						keyboardType="email-address"
						returnKeyType="next"
						selectionColor={AppColors.ovviumYellow}
						underlineColorAndroid="transparent"
						onChangeText={text => this.setState({ email: text })}
						placeholder={msg("login:user")}
					/>
				)}
				
				{!this.props.passwordRecovered && (
					<View style={recoverViewStyles.loginButtonContainer}>
						<TouchableOpacity
							style={{ height: "100%", width: "100%", justifyContent: "center", alignItems: "center" }}
							onPress={() => {
								this.props.onRecoverPassword(this.state.email);
								// this.setState({ isLoading: true });
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
									{msg("login:recoverPassword:button")}
								</Text>
							)}
							{this.props.showIndicator && <ActivityIndicator size="small" color={AppColors.main} />}
						</TouchableOpacity>
					</View>
				)}
				{this.props.passwordRecovered && (
					<View style={{alignItems: "center", marginHorizontal: 30, marginTop: 50}}>
						<EvilIcons size={50} color={AppColors.funnyGreen} name="check"/>
						<Text style={{fontSize:18, fontFamily: AppFonts.regular,marginTop:10, textAlign:'center', color: AppColors.mainText}}>{msg("login:recoverPassword.generated")}</Text>
					</View>
				)}
				<TouchableOpacity style={{alignItems:'center', marginTop: 50}} onPress={this.props.goToLoginView}>
					<Text style={recoverViewStyles.loginSocialText}>{msg("login:alreadyRegistered")}</Text>
				</TouchableOpacity>
			</View>
		);
	}
}
