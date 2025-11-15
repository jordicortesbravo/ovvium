import React from "react";
import { Image, Platform, RegisteredStyle, Text, View, ViewStyle } from 'react-native';
import QRCodeScanner, { Event } from 'react-native-qrcode-scanner';
import { msg } from '../../../services/LocalizationService';
import { AppColors } from '../../styles/layout/AppColors';
import { Header } from '../Header/Header';
import { joinBillStyle } from '../JoinBillView/style';
import AndroidFloatingButton from '../AndroidFloatingButton/AndroidFloatingButton';
import { IconFamily } from '../MultiFamilyIcon/MultifamilyIcon';
import { Button } from "../Button/Button";
import { CURRENT_ENVIRONMENT } from "../../../../resources/Properties";

export interface JoinBillByQRViewProps {
  onLink: (uri: string) => void;
  joining?: boolean;
  nfcSupported: boolean;
  goToNfcView: () => void;
  onSimulatorJoinBill: () => void;
}

export class JoinBillByQRView extends React.Component<JoinBillByQRViewProps> {

    render() {
        return <View style={{height:'100%', backgroundColor: AppColors.white}}>
                    <Header title={msg("bill:join:title")} 
                        goBack={Platform.OS === 'ios' && this.props.nfcSupported ? () => this.props.goToNfcView() : undefined}
                        goBackTitle={Platform.OS === 'ios' && this.props.nfcSupported ? msg("bill:join:nfc:read") : undefined}
                        subtitle={msg("bill:join:qr:text")}
                        format="big" />
                    <QRCodeScanner
                        cameraProps={{ratio: "1:1"}}
                        cameraStyle={joinBillStyle.camera as RegisteredStyle<ViewStyle>}
                        onRead={this.onCodeRead.bind(this)}
                        bottomContent={
                            <View>
                                {CURRENT_ENVIRONMENT == "dev" && 
                                <Button label="Unirse a la mesa" onPress={this.props.onSimulatorJoinBill}/>
                                }
                            </View>
                        }
                    />
                    {Platform.OS === 'android' && this.props.nfcSupported && !this.props.joining &&
                        <AndroidFloatingButton onPress={this.props.goToNfcView} iconName="nfc" iconFamily={IconFamily.MATERIAL_COMMUNITY} iconSize={35}/>
                    }
                </View>
    }


    onCodeRead(event: Event) {
        this.props.onLink(event.data);
    }
}