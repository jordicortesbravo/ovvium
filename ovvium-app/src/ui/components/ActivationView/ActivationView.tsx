import React from "react";
import { Image, View } from "react-native";
import { msg } from '../../../services/LocalizationService';
import { AppColors } from '../../styles/layout/AppColors';
import { Header } from '../Header/Header';
import { activationViewStyle } from './style';

interface ActivationViewProps {
	email: string;
	showIndicator: boolean;
	error: boolean;
}

export class ActivationView extends React.Component<ActivationViewProps> {
	constructor(props: ActivationViewProps) {
		super(props);
	}

	render() {
        return  <View style={{height:'100%'}}>
                    <View style={{height:'100%', backgroundColor: AppColors.white}}>
                        <Header title={"Activa tu cuenta"} 
                            subtitle={msg("login:activate:confirm")+ this.props.email}
                            format="big" />
                            
                        <View style={activationViewStyle.centerContainer}>
                            <Image source={require('../../../../assets/images/icons/emailVerification.png')} style={{width:170, height: 170}} />
                            <Image source={require('../../../../assets/images/icons/logo.png')} style={{width:105, height: 40, position:'absolute', bottom: 0}} />
                        </View>
                    </View>
                </View>
    }

}
