import React from "react";
import { JoinBillByNfcView, JoinBillByNfcViewProps } from '../JoinBillByNfcView/JoinBillByNfcView';
import { JoinBillByQRView, JoinBillByQRViewProps } from '../JoinBillByQRView/JoinBillByQRView';

interface JoinBillViewProps extends JoinBillByNfcViewProps, JoinBillByQRViewProps {
    view: 'nfc' | 'qr';
    autoLaunchNfc: boolean;
    goToNfcView: () => void;
    goToQRView: () => void
    onSimulatorJoinBill: () => void;
}

export class JoinBillView extends React.Component<JoinBillViewProps> {

    joinBillByNfcView?: JoinBillByNfcView;

    render() {
        if(this.props.view === 'qr') {
            return  <JoinBillByQRView
                        onLink={this.props.onLink} 
                        nfcSupported={this.props.nfcSupported}
                        joining={this.props.joining} 
                        goToNfcView={this.props.goToNfcView}
                        onSimulatorJoinBill={this.props.onSimulatorJoinBill}
                    />
        } else {
            return  <JoinBillByNfcView
                        ref={(ref: JoinBillByNfcView) => this.joinBillByNfcView = ref}
                        autoLaunchNfc={this.props.autoLaunchNfc}
                        onLink={this.props.onLink} 
                        joining={this.props.joining} 
                        goToQRView={this.props.goToQRView} 
                    />
        }
    }
} 