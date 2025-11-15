import React from "react";
import { ScrollView, View } from "react-native";
import { Bill } from '../../../model/Bill';
import { PaymentMethod } from '../../../model/PaymentMethod';
import { msg } from '../../../services/LocalizationService';
import { AppColors } from '../../styles/layout/AppColors';
import { Header } from '../Header/Header';
import { PaymentMethodForm } from '../PaymentMethodForm/PaymentMethodForm';
interface BillStep4ViewProps {
    bill: Bill;
    paymentMethod: PaymentMethod;
    goBack: () => void;
    addRunConfirmInterceptor: (interceptor: () => void) => void;
    onSaveButtonPressed: () => void;
    onSavePaymentMethod: (paymentMethod: PaymentMethod) => void;
}

export default class BillStep4View extends React.Component<BillStep4ViewProps> {

    constructor(props: BillStep4ViewProps) {
        super(props);
        this.state = {};
    }

    render() {
        return (
            <View>
                    <ScrollView style={{backgroundColor: AppColors.white, height:'100%'}}>
                        <Header goBack={this.props.goBack} 
                            title={msg("bill:payment:methods")} 
                            doAction={this.props.onSaveButtonPressed}
                            actionTitle={msg("actions:save")} />
                        <PaymentMethodForm paymentMethod={this.props.paymentMethod} 
                            addRunConfirmInterceptor={this.props.addRunConfirmInterceptor}
                            editable={true} 
                            onConfirm={(paymentMethod) => this.props.onSavePaymentMethod(paymentMethod)}/>
                    </ScrollView>
            </View>
        );
    }
}
