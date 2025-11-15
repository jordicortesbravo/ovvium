import React from "react";
import { Text, TextInput, View, Platform } from "react-native";
import { Bill } from '../../../model/Bill';
import { Customer } from '../../../model/Customer';
import { Order } from '../../../model/Order';
import { Tip } from '../../../model/Tip';
import { User } from '../../../model/User';
import { UserBill } from '../../../model/UserBill';
import { msg } from '../../../services/LocalizationService';
import { StringUtils } from '../../../util/StringUtils';
import { AppColors } from '../../styles/layout/AppColors';
import { Header } from '..//Header/Header';
import { billStep3ViewStyle } from '../BillStep3View/style';
import { AppFonts } from "../../styles/layout/AppFonts";

interface BillStep3OtherTipViewProps {
    bill: Bill;
    me: User;
    userBill: UserBill | Bill;
    selectedOrders: Order[];
    customer: Customer;
    defaultTip: Tip;
    goBack: () => void;
    addTip: (tip?: Tip) => void;
}

interface BillStep3OtherTipState {
    tip: Tip;
}

export default class BillStep3OtherTipView extends React.Component<BillStep3OtherTipViewProps, BillStep3OtherTipState> {

    constructor(props: BillStep3OtherTipViewProps) {
        super(props);
        this.state = {tip: new Tip(this.props.defaultTip)};
    }

    render() {
        return (
            <View style={billStep3ViewStyle.container}>
                <Header goBack={this.props.goBack} 
                    title={msg("bill:tip:otherTip")} 
                    actionTitle={msg("actions:continue")}
                    subtitle={Platform.OS == 'ios' ? msg("bill:tip:description"): undefined}
                    doAction={() => this.props.addTip(this.state.tip)} />
                <View style={{marginTop: 50, flexDirection:'row', justifyContent:'center', alignItems:'center',borderBottomWidth:1.5, borderBottomColor: AppColors.ovviumYellow, marginHorizontal: '10%'}}>
                    <TextInput underlineColorAndroid="transparent" 
                        style={{paddingHorizontal: 40, fontSize:40, textAlign: 'right'}}
                        defaultValue={this.props.defaultTip.amount.toFixed(2)}
                        keyboardType="numeric"
                        autoFocus={true}
                        onChangeText={(text:string) => {
                            try {
                                if(StringUtils.isBlank(text)) {
                                    var s = this.props.defaultTip.amount.toString();
                                } else {
                                    s = text.replace(",", ".").replace(/\s/g, "").replace(/-*/, "");
                                    if(StringUtils.isBlank(s)) {
                                        s = this.props.defaultTip.amount.toString();
                                    }
                                    this.state.tip.amount = parseFloat(s);
                                }
                            } catch(error) {
                                this.state.tip.amount = this.props.defaultTip.amount;
                            }
                        }}
                        placeholder={this.props.defaultTip.amount.toFixed(2)} />
                        <Text style={{fontSize: 35, marginLeft: -25, fontFamily: AppFonts.regular}}>{"€"}</Text>
                </View>
            </View>
        );
    }
}
