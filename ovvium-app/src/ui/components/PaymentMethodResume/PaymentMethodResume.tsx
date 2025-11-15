import React from "react";
import { Image, Text, TouchableOpacity, View } from 'react-native';
import { CreditCardType } from '../../../model/enum/CreditCardType';
import { PaymentMethod } from '../../../model/PaymentMethod';
import { msg } from '../../../services/LocalizationService';
import { paymentMethodeResumeViewStyles } from './style';


interface PaymentMethodResumeProps {
    paymentMethod: PaymentMethod;
    onPick: (paymentMethod: PaymentMethod) => void;
}

export class PaymentMethodResume extends React.Component<PaymentMethodResumeProps> {
   
    render() {
        return  <TouchableOpacity style={paymentMethodeResumeViewStyles.container} onPress={() => this.props.onPick(this.props.paymentMethod)}>
                    <View style={paymentMethodeResumeViewStyles.paymentTypeContainer}>
                        {this.renderPaymentMethodImage(this.props.paymentMethod)}
                    </View>
                    <View style={paymentMethodeResumeViewStyles.paymentIdContainer}>
                        <Text style={paymentMethodeResumeViewStyles.paymentIdText}>{this.props.paymentMethod.cardNumber}</Text>
                    </View>
                    <View style={paymentMethodeResumeViewStyles.paymentDetailsContainer}>
                        <Text style={paymentMethodeResumeViewStyles.expirationText}>{this.props.paymentMethod.expiration}</Text>
                        <Text style={paymentMethodeResumeViewStyles.favouriteText}>{this.props.paymentMethod.default ? msg("profile:paymentMethods:favourite") : ""}</Text>
                    </View>
                </TouchableOpacity>
    }

    renderPaymentMethodImage(paymentMethod: PaymentMethod) {
        switch(paymentMethod.brand) {
            case CreditCardType.VISA:
            case CreditCardType.VISA_ELECTRON:
                return <Image source={require('../../../../assets/images/icons/credit-cards/visa.png')} style={{width:65, height: 20}} />
            case CreditCardType.MAESTRO:
                return <Image source={require('../../../../assets/images/icons/credit-cards/maestro.png')} style={{width:65, height: 50, marginTop:-10}} />
            case CreditCardType.MASTERCARD:
                return <Image source={require('../../../../assets/images/icons/credit-cards/mastercard.png')} style={{width:65, height: 50, marginTop:-10}} />
            case CreditCardType.AMEX:
                return <Image source={require('../../../../assets/images/icons/credit-cards/amex.png')} style={{width:65, height: 40, marginTop:-5}} />
            case CreditCardType.DISCOVER:
                return <Image source={require('../../../../assets/images/icons/credit-cards/discover.png')} style={{width:65, height: 20}} />
            case CreditCardType.DINERS:
                return <Image source={require('../../../../assets/images/icons/credit-cards/diners.png')} style={{width:65, height: 20}} />
            case CreditCardType.JCB:
                return <Image source={require('../../../../assets/images/icons/credit-cards/jcb.png')} style={{width:65, height: 20}} />

        }
    }

}