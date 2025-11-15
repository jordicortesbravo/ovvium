import React from "react";
import { ScrollView, View } from "react-native";
import { PaymentMethod } from '../../../model/PaymentMethod';
import { msg } from '../../../services/LocalizationService';
import { Header } from '..//Header/Header';
import { billStep5ViewStyle } from '../BillStep5View/style';
import { PaymentMethodResume } from '../PaymentMethodResume/PaymentMethodResume';

interface BillStep5PickPaymentMethodProps {
    paymentMethodList: Array<PaymentMethod>;
    onPickPaymentMethod: (paymentMethod: PaymentMethod) => void;
    goBack: () => void;
}


export default class BillStep5PickPaymentMethodView extends React.Component<BillStep5PickPaymentMethodProps> {

    render() {
        return (
            <ScrollView style={billStep5ViewStyle.container}>
                <Header goBack={this.props.goBack} format="big" goBackTitle={msg("actions:back")} title={msg("bill:payment:pickPaymentMethod")} />
                <View>
                    {this.renderPaymentMethods()}
                </View>
            </ScrollView>
        );
    }

    renderPaymentMethods() {
        return this.props.paymentMethodList.map((paymentMethod: PaymentMethod, index:number) => {
            return  <PaymentMethodResume paymentMethod={paymentMethod} onPick={this.props.onPickPaymentMethod} key={"paymentMethod-" + index} />
        });
    }

   
}
