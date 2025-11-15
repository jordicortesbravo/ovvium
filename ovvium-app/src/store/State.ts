import { Bill } from '../model/Bill';
import { Customer } from '../model/Customer';
import { Invoice } from '../model/Invoice';
import { Order } from '../model/Order';
import { PaymentMethod } from '../model/PaymentMethod';
import { Product } from '../model/Product';
import { InvoicePage } from '../model/response/InvoicePage';
import { Session } from '../model/Session';
import { Tip } from '../model/Tip';
import { User } from '../model/User';
import { UserBill } from '../model/UserBill';

export interface AppState { 
    sessionState: SessionState;
    productsState: ProductsState;
    billState: BillState;
    cartState: CartState;
    profileState: ProfileState;
    executionState: ExecutionState;
    onboardingState: OnboardingState;
}

export interface ProductsState {
    products: Product[];
    selectedProduct?: Product;
}

export interface SessionState {
    user?: User;
    session?: Session;
    passwordRecovered?: boolean;
}

export interface BillState {
    bill?: Bill;
    customer?: Customer;
    userBill?: UserBill | Bill;
    usersToPay?: Array<User>;
    ordersToPay?: Array<Order>;
    tip?: Tip;
    lastInvoice?: Invoice;
    invoicePage?: InvoicePage;
}

export interface CartState {
    orders: Array<Order>;
}

export interface ExecutionState {
    errors: any[];
    showIndicator: boolean;
    autoLaunchNfc: boolean;
}

export interface ProfileState {
    selectedPaymentMethod?: PaymentMethod;
    allergens: Array<string>;
    foodPreferences: Array<string>;
    paymentMethodList: Array<PaymentMethod>;
}

export interface OnboardingState {
    showOnboarding: boolean;
    shownTricks: string[];
}

export const initialState: AppState = {
    //FIXME: jcortes (12/07/2019)
    //Aquí se están haciendo unas inicializaciones para que no pete al re-renderizar las pantallas que existan al resetear el state cuando se hace el logout. Realmente
    //esto es un pésima solución ya que se están poniendo unos valores iniciales sin mucho sentido. No obstante, esto requiere un pensamiento profundo y probablemente llevará
    //a un refactor que puede tener una envergadura importante. De todas formas a día de hoy parece afectar "solo" al logout. No obstante es un error de base!
    //Para solventarlo se debería ser consecuente con los campos obligatorios y con los constructores en todos los sitios y también tenerlo en cuenta en todos los puntos en los
    //que se haga uso de esos objetos para operar en consecuencia a los posibles valores que pueden adoptar.
    sessionState: {},
    productsState: {
        products: []
    },
    billState: {
    },
    cartState: {
        orders: []
    },
    profileState: {allergens: [], foodPreferences: [], paymentMethodList:[] },
    executionState: { showIndicator: false, errors: [], autoLaunchNfc: true},
    onboardingState: {
        showOnboarding: true,
        shownTricks: []
    }
};