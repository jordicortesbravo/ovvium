import { PaymentMethodType } from './enum/PaymentMethodType';
import { UserCardDataResponse } from './response/UserCardDataResponse';

export class PaymentMethod {
    id: string;
    type: PaymentMethodType;
    cardNumber: string;
    expiration: string;
    cardType?: string;
    brand: string;
    default: boolean;
    pciTemporalToken?: string;


    constructor(paymentMethod: PaymentMethod) {
        this.id = paymentMethod.id;
        this.type = paymentMethod.type;
        this.cardNumber = paymentMethod.cardNumber;
        this.expiration = paymentMethod.expiration;
        this.cardType = paymentMethod.cardType;
        this.brand = paymentMethod.brand;
        this.default = paymentMethod.default;
    }

    static from(userCardDataResponse: UserCardDataResponse): PaymentMethod {
        return new PaymentMethod({
            id: userCardDataResponse.pciDetailsId,
            type: PaymentMethodType.APP_CREDIT_CARD,
            cardNumber: userCardDataResponse.pan.split("-").join("").split("X").join("·"),
            cardType: userCardDataResponse.type,
            expiration: userCardDataResponse.expiryDate,
            brand: userCardDataResponse.brand,
            default: false
        });
    }
}