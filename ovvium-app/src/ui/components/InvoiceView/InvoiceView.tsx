import React from "react";
import { ScrollView, View } from "react-native";
import { Invoice } from "../../../model/Invoice";
import { msg } from '../../../services/LocalizationService';
import { Header } from '../Header/Header';
import { InvoiceResume } from "../InvoiceResume/InvoiceResume";
import { AppColors } from "../../styles/layout/AppColors";


interface InvoiceViewProps {
    invoice: Invoice;
    goBack: () => void;
}

export default class InvoiceView extends React.Component<InvoiceViewProps> {

    render() {
        return (
                <ScrollView style={{height:'100%', backgroundColor: AppColors.white}}>
                    <Header goBack={this.props.goBack} goBackTitle={msg("actions:back")} title={msg("bill:invoice:detail")} format="big" subtitle={msg("bill:invoice:subtitle")}/>
                    <InvoiceResume invoice={this.props.invoice} />
                </ScrollView>
        );
    }
}
