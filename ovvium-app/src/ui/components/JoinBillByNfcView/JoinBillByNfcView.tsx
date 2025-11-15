import React from "react";
import { Image, Platform, Text, View, ActivityIndicator } from 'react-native';
import { msg } from '../../../services/LocalizationService';
import { StringUtils } from '../../../util/StringUtils';
import { AppColors } from '../../styles/layout/AppColors';
import { Header } from '../Header/Header';
import MultifamilyIcon, { IconFamily } from '../MultiFamilyIcon/MultifamilyIcon';
import { joinBillStyle } from '../JoinBillView/style';
import AndroidFloatingButton from '../AndroidFloatingButton/AndroidFloatingButton';
import { NfcAdapter } from '../NfcAdapter/NfcAdapter';
import { Button } from "../Button/Button";
import { buttonStyles } from "../Button/style";
import { AppScreens } from "../../navigation/AppScreens";
import { LoadingView } from "../LoadingView/LoadingView";
import { useNavigationState, NavigationContext } from "@react-navigation/native";
import { AppFonts } from "../../styles/layout/AppFonts";

export interface JoinBillByNfcViewProps {
  joining?: boolean;
  autoLaunchNfc: boolean;
  onLink: (uri: string) => void;
  goToQRView: () => void;
}

export interface JoinBillByNfcViewState {
    nfcInitialized?: boolean;
}

export class JoinBillByNfcView extends React.Component<JoinBillByNfcViewProps, JoinBillByNfcViewState> {

    static contextType = NavigationContext;

    nfcAdapter?: NfcAdapter;

    constructor(props: JoinBillByNfcViewProps) {
        super(props);
        this.state = {};
    }

    componentDidMount() {
        if(!this.state.nfcInitialized && this.props.autoLaunchNfc) {
            this.startNfc();
        }
    }

    render() {
        return  <View style={{height:'100%'}}>
                    <View style={{height:'100%', backgroundColor: AppColors.white}}>
                        <Header title={msg("bill:join:title")} 
                            subtitle={msg("bill:join:nfc:text")}
                            actionTitle={Platform.OS === 'ios' ? msg("bill:join:qr:read") : undefined}
                            doAction={Platform.OS === 'ios' ? () => this.props.goToQRView() : undefined}
                            format="big" />
                            
                        <NfcAdapter ref={(ref: NfcAdapter) => this.nfcAdapter = ref} onReadTag={this.props.onLink} />
                        <View style={joinBillStyle.centerContainer}>
                            {this.props.joining &&
                                <View style={{justifyContent:'center', alignItems:'center', marginTop:-220}}>
                                    <ActivityIndicator color={AppColors.main} size="large" style={{marginTop: 40}}/>
                                    <Text style={{fontFamily:AppFonts.regular, fontSize: 15, color:AppColors.mainText, marginTop:10, textAlign: 'center'}}>{msg("bill:join:joining")} </Text>
                                </View>
                            }
                            {!this.props.joining && 
                                <View style={{marginTop: -150, justifyContent:'center', alignItems:'center'}}>
                                    <MultifamilyIcon style={{marginRight: 20}} family={IconFamily.MATERIAL_COMMUNITY}  name="nfc" size={220} color={AppColors.gray} />
                                    <Text style={{color: AppColors.gray, fontFamily: AppFonts.regular, fontSize:16, marginTop: -30}}>{msg("bill:join:nfc:text2")}</Text>
                                </View> 

                            }
                        </View>
                        {Platform.OS === 'android' &&
                            <AndroidFloatingButton onPress={this.props.goToQRView} iconName="camera" iconFamily={IconFamily.FEATHER} />
                        }
                        {Platform.OS === 'ios' &&
                            <View style={{alignItems: 'center', position:'absolute', bottom:0, width: '100%', backgroundColor:'white', zIndex:2, height: 80, justifyContent: 'center'}}>
                                <Button label={msg("bill:join:nfc:explore")}  onPress={this.startNfc.bind(this)} />
                            </View>
                        }
                    </View>
                </View>
    }

    startNfc() {
        this.setState({nfcInitialized: true})
        if(this.nfcAdapter) {
            this.nfcAdapter.listen();
        }
    }
}