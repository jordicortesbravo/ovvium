import React from "react";
import { FlatList, ListRenderItemInfo, ScrollView, Text, View } from "react-native";
import { Bill } from '../../../model/Bill';
import { UserBill } from '../../../model/UserBill';
import { Order } from '../../../model/Order';
import { User } from '../../../model/User';
import { getPendingAmount, getTotalAmount, mapOrdersByProduct } from '../../../services/BillService';
import { billStep2ViewStyle } from './style';
import { msg } from '../../../services/LocalizationService';
import { AppColors } from '../../styles/layout/AppColors';
import { Header } from '../Header/Header';
import { Button } from '../Button/Button';
import { ArrayUtils } from '../../../util/ArrayUtils';
import { BillOrderGroupListItem } from '../BillOrderGroupListItem/BillOrderGroupListItem';
import { StringUtils } from '../../../util/StringUtils';
import { UserImageProfile } from '../UserImageProfile/UserImageProfile';
import { Tricks } from "../../../model/enum/Tricks";
import Swipeable from "../../containers/widgets/Swipeable";
import { AppFonts } from "../../styles/layout/AppFonts";

interface BillStep2ViewProps {
    bill: Bill;
    userBill: UserBill | Bill;
    me: User;
    selectedUsers: User[];
    selectedOrders: Order[];
    goBack: () => void;
    goToTip: () => void;
    toggleMember: (user: User) => void;
}

export class BillStep2View extends React.Component<BillStep2ViewProps> {

    render() {
        const pendingAmount = getPendingAmount(this.props.selectedOrders);
        const buttonStyle =  pendingAmount == 0 ? billStep2ViewStyle.tipButtonContainer: billStep2ViewStyle.payButtonContainer;
        const totalAmount = getTotalAmount(this.props.selectedOrders).toFixed(2)+'€';
        const buttonLabel = pendingAmount == 0 ? msg("bill:payment:tip") : msg("actions:pay")+ " " + pendingAmount.toFixed(2)+'€';
        return (
            <View style={billStep2ViewStyle.container}>
                <View style={{height:'100%'}}>
                    <ScrollView style={{marginBottom:100, height:'100%'}}>
                        <Header goBack={this.props.goBack} goBackTitle={msg("actions:back")} format="big" title={this.getBillResumeTitle()} subtitle={msg("bill:selectBill")}/>
                        <View style={{borderBottomColor: 'rgba(0,0,0,0.035)',borderBottomWidth: 1, marginHorizontal:'5%', marginTop: 20}}/>
                        <ScrollView horizontal={true} showsHorizontalScrollIndicator={false} style={[billStep2ViewStyle.membersContainer,{marginVertical: 10, marginHorizontal:10}]}>
                            {this.renderUsers()}
                        </ScrollView>
                        <View style={{borderBottomColor: 'rgba(0,0,0,0.035)',borderBottomWidth: 1, marginHorizontal:'5%'}}/>
                        <Text style={{fontFamily: AppFonts.medium, fontSize:18,color: AppColors.mainText, marginVertical: 20, marginLeft: 30}}>{msg("bill:detail")}</Text>
                        <FlatList  data={Array.from(mapOrdersByProduct(this.props.selectedOrders).values())} renderItem={this.renderOrders} 
                            style={{marginTop: 10}}
                            keyExtractor={(item: Array<Order>, index:number) => 'orders_' + index.toString()} />
                        <View style={billStep2ViewStyle.totalPriceContainer}>
                            <Text style={billStep2ViewStyle.totalPriceTextLeft}>{msg("bill:total")}</Text>
                            <Text style={billStep2ViewStyle.totalPriceTextRight}>{totalAmount}</Text>
                        </View>
                        <View style={[billStep2ViewStyle.totalPriceContainer]}>
                            <Text style={billStep2ViewStyle.pendingPriceTextLeft}>{msg("bill:pending")}</Text>
                            <Text style={[billStep2ViewStyle.totalPendingText, this.getPendingPaymentStyle(pendingAmount)]}>{this.getPendingPaymentText(pendingAmount)}</Text>
                        </View>
                    </ScrollView>
                    <Swipeable message={msg("onboarding:tricks:billStep2")} id={Tricks.BILL_STEP_2}/>
                </View>
                <View style={{alignItems: 'center', position:'absolute', bottom:0, width: '100%', backgroundColor:'white', zIndex:2, height: 80, justifyContent: 'center'}}>
                    <Button label={buttonLabel} containerStyle={buttonStyle}  onPress={this.props.goToTip} />
                </View>
            </View>
            
        );
    }

    renderOrders(info: ListRenderItemInfo<Array<Order>>) {
        var orders = info.item;
        if(!orders || orders.length == 0) {
            return <View/>
        }
        var product = ArrayUtils.first(orders).product;
        return <BillOrderGroupListItem orders={orders} product={product} />
    }

    renderUsers() {
        return this.props.bill.members.sort((u1:User, u2:User) => {
            return u1.id == this.props.me.id ? -1 : u2.id == this.props.me.id ? 1 : u1.name.localeCompare(u2.name);
        }).map((user: User) => {
            const selected = ArrayUtils.contains(this.props.selectedUsers, user, "id");
            return <UserImageProfile key={"user" + user.id} user={user} showName={true} touchable={true} selected={selected} onPress={() => this.props.toggleMember(user)}/>
        });
    }

    getBillResumeTitle() {
        if(this.isMyBill()) {
            return msg("bill:myBill");
        } else if(this.props.userBill instanceof Bill) {
            return msg("bill:totalBill");
        }
        return StringUtils.abbreviate(msg("bill:billOf") + (this.props.userBill ? this.props.userBill.user.name : this.props.me), 20);
    }

    isMyBill() {
        return this.props.userBill && this.props.userBill instanceof UserBill && this.props.userBill.user.id == this.props.me.id;
    }

    getPendingPaymentStyle(pendingPayment: number) {
        return pendingPayment == 0 ? {color: AppColors.funnyGreen, fontFamily:AppFonts.bold} : {color: AppColors.red};
    }

    getPendingPaymentText(pendingPayment: number): string {
        return pendingPayment == 0 ? msg("bill:paid") : pendingPayment.toFixed(2)+'€';
    }
}
