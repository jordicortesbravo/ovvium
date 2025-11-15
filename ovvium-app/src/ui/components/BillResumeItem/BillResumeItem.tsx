import React from 'react';
import { Text, TouchableHighlight, View } from "react-native";
import MaterialIcon from "react-native-vector-icons/MaterialIcons";
import { Bill } from '../../../model/Bill';
import { Order } from '../../../model/Order';
import { Tip } from '../../../model/Tip';
import { User } from '../../../model/User';
import { UserBill } from '../../../model/UserBill';
import { getPendingAmount, getTotalAmount, getUserOrders } from '../../../services/BillService';
import { msg } from '../../../services/LocalizationService';
import { AppColors } from '../../styles/layout/AppColors';
import MultifamilyIcon, { IconFamily } from '../MultiFamilyIcon/MultifamilyIcon';
import { UserImageProfile } from '../UserImageProfile/UserImageProfile';
import { billResumeItemStyle } from './style';
import { AppFonts } from '../../styles/layout/AppFonts';

interface BillResumeProps {
    bill: Bill;
    userBill?: UserBill;
    me: User; 
    ordersByProduct: Map<string, Array<Order>>;
    tip?: Tip;
    onSelectBill: (bill: UserBill | Bill) => void;
}

export class BillResumeItem extends React.Component<BillResumeProps> {

    render() {
        const bill = this.props.userBill ? this.props.userBill : this.props.bill;
        const tipAmount = this.props.tip ? this.props.tip.amount : 0;
        const showPayed = getUserOrders(this.props.bill, [this.props.me]).length > 0 && getPendingAmount(bill) == 0;
        const peopleColors = AppColors.userPlaceholderColors[2];
        return (
            <TouchableHighlight underlayColor={AppColors.touchableOpacity} onPress={() => this.props.onSelectBill(bill)}>
                <View>
                    <View style={billResumeItemStyle.card}>
                        <View style={billResumeItemStyle.avatarBox}>
                            {this.props.userBill && (
                                <UserImageProfile user={this.props.userBill.user} />
                            )}
                            {!this.props.userBill && (
                                <View style={[{backgroundColor: peopleColors.soft,borderRadius: 27.5,height: 52,width: 52,justifyContent:'center',alignItems:'center'}]}>
                                    <MultifamilyIcon family={IconFamily.FEATHER} name="users" size={30} style={[billResumeItemStyle.avatar, {color: peopleColors.hard, borderWidth:0}]}/>
                                </View>
                            )}
                        </View>
                        <View style={billResumeItemStyle.descriptionContainer}>
                            <Text style={this.getBillStyle(billResumeItemStyle.titleText)}>{this.getBillResumeTitle()}</Text>
                            <Text style={billResumeItemStyle.descriptionText}>{this.getBillResumeDescription()}</Text>
                        </View>
                        <View style={billResumeItemStyle.rightContainer}>
                            <View style={billResumeItemStyle.priceContainer} >
                                <Text style={[this.getBillStyle(billResumeItemStyle.priceText), showPayed ? {textDecorationLine: 'line-through'}: {}]}>{getTotalAmount(bill).toFixed(2)+'€'}</Text>
                            </View>
                            <View style={billResumeItemStyle.secondaryPriceContainer}>
                                <Text style={[billResumeItemStyle.secondaryPriceText, this.getPendingPaymentStyle(getPendingAmount(bill))]}>{this.getPendingPaymentText(getPendingAmount(bill), showPayed)}</Text>
                            </View>
                        </View> 
                        <View style={billResumeItemStyle.arrowContainer}>
                            <MaterialIcon name="chevron-right" size={22} color='#D1D1D6'/>
                        </View>
                    </View>
                    <View style={{borderBottomColor: 'rgba(0,0,0,0.035)',borderBottomWidth: 1, marginHorizontal:'5%'}}/>
                </View>
            </TouchableHighlight>
        );
    }

    getBillResumeTitle() {
        if(this.isMyBill()) {
            return msg("bill:myBill");
        } else if(!this.props.userBill) {
            return msg("bill:totalBill");
        }
        return this.props.userBill.user.name;
    }

    getBillResumeDescription() {
        var resumeMessage = "";
        this.props.ordersByProduct.forEach((orders:Array<Order>, productId: string) => {
            resumeMessage += orders.length + " x " + orders[0].product.name + ", "
        });
        if(resumeMessage.length > 0) {
            resumeMessage = resumeMessage.substring(0,resumeMessage.length-2);
        }

        if(resumeMessage.length > 55) {
            resumeMessage = resumeMessage.substring(0, 55) + "...";
        }

        if((this.props.userBill && this.props.userBill.orders.length == 0) || resumeMessage.length == 0) {
            resumeMessage = msg("bill:empty")
        }

        return resumeMessage;
    }

    getPendingPaymentStyle(pendingPayment: number) {
        return pendingPayment == 0 ? {color: AppColors.funnyGreen, fontFamily:AppFonts.bold} : {color: AppColors.red};
    }

    getPendingPaymentText(pendingPayment: number, showPayed: boolean): string {
        if(this.props.userBill) {
            return showPayed ? "Pagado" : "";
        }
        return showPayed ? "Pagado" : pendingPayment.toFixed(2)+'€';
    }

    getBillStyle(style: any) : any {
        return this.isMyBill() ? [style, {fontFamily: AppFonts.bold}] : style;
    }

    isMyBill() {
        return this.props.userBill && this.props.userBill.user.id == this.props.me.id;
    }
}