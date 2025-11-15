import React from "react";
import { ScrollView, Switch, View, Platform } from 'react-native';
import { PaymentMethod } from '../../../model/PaymentMethod';
import { msg } from '../../../services/LocalizationService';
import { AppColors } from '../../styles/layout/AppColors';
import { Header } from '../Header/Header';
import { MenuItem } from '../MenuItem/MenuItem';
import { PaymentMethodForm } from '../PaymentMethodForm/PaymentMethodForm';
import { IconFamily } from '../MultiFamilyIcon/MultifamilyIcon';

interface EditPaymentMethodViewProps {
    paymentMethod: PaymentMethod;
    editable: boolean;
    addRunConfirmInterceptor: (interceptor: () => void) => void;
    onSaveButtonPressed: () => void;
    onSavePaymentMethod: (paymentMethod: PaymentMethod) => void;
    onDeletePaymentMethod: (paymentMethod: PaymentMethod) => void;
    goBack: () => void;
}

interface EditPaymentMethodViewState {
    paymentMethod: PaymentMethod;
}

export class EditPaymentMethodView extends React.Component<EditPaymentMethodViewProps, EditPaymentMethodViewState> {
    
    constructor(props: EditPaymentMethodViewProps) {
        super(props);
        this.state = {paymentMethod: props.paymentMethod};
    }

    render() {
        return  <View>
                    <ScrollView style={{backgroundColor: AppColors.white, height:'100%'}}>
                        <Header goBack={this.props.goBack} 
                            title={msg("profile:paymentMethods:title")} 
                            actionTitle={msg("actions:save")}
                            doAction={() => this.props.onSaveButtonPressed()} />
                        <PaymentMethodForm paymentMethod={this.props.paymentMethod} 
                            editable={this.props.editable} 
                            addRunConfirmInterceptor={this.props.addRunConfirmInterceptor}
                            onConfirm={this.onConfirm.bind(this)}/>
                        <View style={{height:'100%', backgroundColor: AppColors.white, marginBottom:150}}>
                            <MenuItem iconColor={AppColors.userPlaceholderColors[7].soft} 
                                iconTextColor={AppColors.userPlaceholderColors[7].hard} 
                                hideArrow={true}
                                iconFamily={IconFamily.FEATHER} 
                                iconName="star" title={msg("profile:paymentMethods:favourite")} 
                                subtitle={"Marca la tarjeta como favorita"} 
                                rightElement={
                                    <Switch value={this.state.paymentMethod.default} 
                                        thumbColor={Platform.OS == 'ios' ? undefined : this.state.paymentMethod.default ? AppColors.ovviumYellow : '#DCDCDC'} 
                                        trackColor={Platform.OS == 'ios' ? undefined : {true: AppColors.userPlaceholderColors[7].soft, false:'gray'}} 
                                        onValueChange={(selected: boolean) => {
                                        var pm = new PaymentMethod(this.state.paymentMethod);
                                        pm.default = selected;
                                        this.setState({paymentMethod: pm});
                                    }}/>
                                } />
                            {!this.props.editable && <MenuItem iconColor={AppColors.userPlaceholderColors[0].soft} 
                                iconTextColor={AppColors.userPlaceholderColors[0].hard} 
                                iconFamily={IconFamily.FEATHER} 
                                hideArrow={true}
                                iconName="trash-2" title={msg("profile:paymentMethods:remove:label")} 
                                subtitle={"Borra este método de pago"} 
                                onPress={() => this.props.onDeletePaymentMethod(this.props.paymentMethod)} />
                            }
                        </View>
                    </ScrollView>
                </View>
    }

    onConfirm(paymentMethod: PaymentMethod) {
        if(this.props.editable) {
            this.props.onSavePaymentMethod(paymentMethod);
        } else {
            this.props.onSavePaymentMethod(this.state.paymentMethod);
        }
    } 
}
