import React from "react";
import { Platform, ScrollView, Text, TouchableOpacity, View } from 'react-native';
import { PaymentMethod } from '../../../model/PaymentMethod';
import { msg } from '../../../services/LocalizationService';
import { AppColors } from '../../styles/layout/AppColors';
import { Header } from '../Header/Header';
import MultifamilyIcon, { IconFamily } from '../MultiFamilyIcon/MultifamilyIcon';
import AndroidFloatingButton from '../AndroidFloatingButton/AndroidFloatingButton';
import { PaymentMethodResume } from '../PaymentMethodResume/PaymentMethodResume';
import { AppFonts } from "../../styles/layout/AppFonts";

interface PaymentMethodsViewProps {
    paymentMethodList: Array<PaymentMethod>;
    goBack: () => void;
    onModifyPaymentMethod: (paymentMethod: PaymentMethod) => void;
    onAddPaymentMethod: () => void;
}

export class PaymentMethodsView extends React.Component<PaymentMethodsViewProps> {
    
    render() {
        return  <View style={{height:'100%', backgroundColor:AppColors.white}}>
                    <Header goBack={this.props.goBack} goBackTitle={msg("actions:back")} format={"big"}
                        title={msg("profile:paymentMethods:title")} subtitle={msg("profile:paymentMethods:subtitle")} />
                        {this.renderAddPaymentMethod()}
                    <ScrollView>
                        {this.renderPaymentMethods()}
                    </ScrollView>
                </View>  
        
    }

    renderPaymentMethods() {
        return this.props.paymentMethodList.map((paymentMethod: PaymentMethod, index:number) => {
            return  <PaymentMethodResume paymentMethod={paymentMethod} onPick={this.props.onModifyPaymentMethod} key={"paymentMethod-" + index} />
        });
    }

    renderAddPaymentMethod() {
        if(Platform.OS == 'ios') {
            return  <TouchableOpacity style={{borderWidth:1, borderColor: AppColors.separator, borderRadius:8, marginHorizontal:10, marginVertical: 10, flexDirection:'row', height:80, justifyContent:'center'}} onPress={this.props.onAddPaymentMethod}>
                        <View style={{flexDirection:'row', position:'absolute', right:20, top:25 }}>
                            <Text style={{fontSize:17, color: AppColors.gray, marginHorizontal: 10, marginVertical:5, fontFamily: AppFonts.regular}}>{msg("profile:paymentMethods:add")}</Text>
                            <MultifamilyIcon family={IconFamily.ION} name="ios-add-circle-outline" color={AppColors.main} size={30}/>
                        </View>
                    </TouchableOpacity>

        } else {
            return <AndroidFloatingButton onPress={this.props.onAddPaymentMethod} iconName="plus" iconFamily={IconFamily.FEATHER} />
        }
    }
}
