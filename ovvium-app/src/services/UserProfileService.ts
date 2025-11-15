import { CreditCardType } from '../model/enum/CreditCardType';
import { PaymentMethod } from '../model/PaymentMethod';
import { msg } from './LocalizationService';

export function getDefaultPaymentMethod(paymentMethodList: Array<PaymentMethod>): PaymentMethod | undefined {
    if(paymentMethodList.length > 0) {
        for(var i=0; i < paymentMethodList.length; i++) {
            if(paymentMethodList[i].default) {
                return paymentMethodList[i];
            }
        }
    }
    return undefined;
}


