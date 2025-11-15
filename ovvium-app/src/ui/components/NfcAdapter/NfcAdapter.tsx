import React from "react";
import NfcManager, { Ndef, NdefRecord, TagEvent, NfcEvents } from 'react-native-nfc-manager';
import { isGenericTypeAnnotation } from "@babel/types";

interface NfcAdapterProps {
    onReadTag: (uri: string) => void;
    onError?: (error: any) => void;
 }

interface NfcAdapterState {
    started: boolean
}

export class NfcAdapter extends React.Component<NfcAdapterProps, NfcAdapterState> {

    constructor(props: NfcAdapterProps) {
        super(props);
        this.state ={started: false}
    }

    render() {
        return null;
    }

    componentWillUnmount() {
        NfcManager.setEventListener(NfcEvents.DiscoverTag, null);
        NfcManager.unregisterTagEvent().catch(() => 0);
    }

    listen() {
        if(!this.state.started) {
            this.start();
        }
        try {
            NfcManager.registerTagEvent();
        } catch(error) {
            NfcManager.unregisterTagEvent().catch(() => 0);
        }
    }

    start() {
        NfcManager.start();
        this.setState({started: true});
        NfcManager.setEventListener(NfcEvents.DiscoverTag, (tag:any) => {
            var uri = this.parse(tag);
            if(uri) {
                this.props.onReadTag(uri);
            }
            NfcManager.unregisterTagEvent().catch(() => 0);
        });
        NfcManager.setEventListener(NfcEvents.SessionClosed, () => {
            NfcManager.unregisterTagEvent().catch(() => 0);
        });
    }

    private parse(tag: TagEvent) {
        let parsed = null;
        if (tag.ndefMessage && tag.ndefMessage.length > 0) {
            const ndefRecords = tag.ndefMessage;
            parsed = ndefRecords.map(this.decodeNdefRecord);
        }
        if(parsed && parsed.length == 1) {
            return parsed[0];
        }
    }

    private decodeNdefRecord(record: NdefRecord) {
        if (Ndef.isType(record, Ndef.TNF_WELL_KNOWN, Ndef.RTD_URI)) {
            return Ndef.uri.decodePayload(record.payload as any);
        }
    }
}