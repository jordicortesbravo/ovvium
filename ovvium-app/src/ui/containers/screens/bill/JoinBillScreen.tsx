import { NavigationProp, Route } from "@react-navigation/core";
import React from "react";
import NfcManager from 'react-native-nfc-manager';
import { connect } from 'react-redux';
import { AnyAction, bindActionCreators, Dispatch } from 'redux';
import { createOrJoinBillActionCreator } from '../../../../actions/BillActions';
import { Bill } from '../../../../model/Bill';
import { BillStatus } from '../../../../model/enum/BillStatus';
import { User } from '../../../../model/User';
import { AppState } from '../../../../store/State';
import { CryptoUtils } from '../../../../util/CryptoUtils';
import { JoinBillView } from '../../../components/JoinBillView/JoinBillView';
import { AppScreens } from '../../../navigation/AppScreens';
import { properties } from "../../../../../resources/Properties";

export interface JoinBillScreenProps {
    bill?: Bill;
    user: User;
    autoLaunchNfc: boolean;
    navigation: NavigationProp<any>;
    createOrJoinBill: (tagId: string, user: User) => Promise<Bill>;
}

export interface JoinBillScreenState {
  nfcSupported: boolean;
  forcedView?: 'qr' | 'nfc';
  joining: boolean;
}
 
const BASE_TAG_URI = "https://ovvium.com/jb?";

export class JoinBillScreen extends React.Component<JoinBillScreenProps, JoinBillScreenState> {

    static navigationOptions = {
        header: null
    }

    constructor(props: JoinBillScreenProps) {
        super(props);
        this.state = {nfcSupported: false, joining: false}
    }

    UNSAFE_componentWillMount() {
        this.checkNfc();
        
    }

    render() {
        return <JoinBillView
                    view={this.state.forcedView ? this.state.forcedView : this.state.nfcSupported ? 'nfc' : 'qr'}
                    nfcSupported={this.state.nfcSupported}
                    onLink={this.processUri.bind(this)} 
                    joining={this.state.joining} 
                    autoLaunchNfc={this.props.autoLaunchNfc}
                    goToNfcView={() => this.props.navigation.navigate(AppScreens.JoinBill, {forceStartNfc: true})}
                    goToQRView={() => this.props.navigation.navigate(AppScreens.JoinBillByQR)}
                    onSimulatorJoinBill={this.onSimulatorJoinBill.bind(this)}
            />
    }

    private onSimulatorJoinBill() {
        var encryptedUri = CryptoUtils.encrypt("tid=KNgEnSbs4G", properties.encryption.key);
        this.processUri(BASE_TAG_URI + encryptedUri)
    }

    private async checkNfc() {

        let supported = await NfcManager.isSupported();
        // let enabled = await NfcManager.isEnabled();
        this.setState({nfcSupported: true});
        if(supported ) {
            this.setState({nfcSupported: true});
        } else {
            this.setState({nfcSupported: false});
        }
    }

    private async processUri(uri: string) {
        var decryptedUri = this.decrypt(uri);
        if(decryptedUri) {
            let splitted = decryptedUri.split(BASE_TAG_URI + "tid=");
            if (splitted.length == 2 && splitted[1]) {     
                this.setState({joining: true});
                await this.props.createOrJoinBill(splitted[1], this.props.user);
            }
        }
    }

    private decrypt(uri: string) {
        let splitted = uri.split("?"); 
        return splitted[0] + "?" + CryptoUtils.decrypt(splitted[1], properties.encryption.key);
    }
}

function mapStateToProps(state: AppState): JoinBillScreenProps {
    return {
      user: state.sessionState.user,
      bill: state.billState.bill,
      autoLaunchNfc: state.executionState.autoLaunchNfc
    } as JoinBillScreenProps;
  }

function mapDispatchToProps(dispatch: Dispatch<AnyAction>) {
    return bindActionCreators(
      {
        createOrJoinBill: createOrJoinBillActionCreator
      },
      dispatch
    );
  }
  
  const JoinBillContainer = connect(
    mapStateToProps,
    mapDispatchToProps
  )(JoinBillScreen);
  
  export default JoinBillContainer;
  