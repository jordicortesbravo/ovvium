import React from "react";
import { ScrollView, Text, View } from "react-native";
import { Bill } from '../../../model/Bill';
import { Customer } from '../../../model/Customer';
import { Order } from '../../../model/Order';
import { Tip } from '../../../model/Tip';
import { User } from '../../../model/User';
import { UserBill } from '../../../model/UserBill';
import { getPendingAmount } from '../../../services/BillService';
import { msg } from '../../../services/LocalizationService';
import { AppColors } from '../../styles/layout/AppColors';
import { Header } from '..//Header/Header';
import { billStep2ViewStyle } from '../BillStep2View/style';
import { Button } from '../Button/Button';
import { CircularButtonGroup } from '../CircularButtonGroup/CircularButtonGroup';
import { ImageWithPlaceholder } from '../ImageWithPlaceholder/ImageWithPlaceholder';
import { StarRatings } from '../StarRatings/StarRatings';
import { billStep3ViewStyle } from './style';
import { AppFonts } from "../../styles/layout/AppFonts";

interface BillStep3ViewProps {
    bill: Bill;
    me: User;
    userBill: UserBill | Bill;
    selectedOrders: Order[];
    customer: Customer;
    proposedTips: Tip[];
    selectedTip?: Tip;
    goBack: () => void;
    goToOtherTip: () => void;
    goNext: () => void;
    addTip: (tip?: Tip) => void;
}

export default class BillStep3View extends React.Component<BillStep3ViewProps> {

    constructor(props: BillStep3ViewProps) {
        super(props);
        props.addTip(props.proposedTips[1]);
    }

    render() {
        return (
            <View>
                <ScrollView style={billStep3ViewStyle.container}>
                    <Header goBack={this.props.goBack} goBackTitle={msg("actions:back")}
                        format={"big"} title={msg("bill:tip:title")} subtitle={msg("bill:tip:description")} />

                    <View style={{ marginTop: 30, marginBottom: 120, alignItems: 'center', width: '100%' }}>
                        {this.props.customer &&
                            <ImageWithPlaceholder source={this.props.customer.imageUrl} imageStyle={{ width: 70, height: 70, borderRadius: 35 }} imagePlaceholderSize={30}
                                showPhotoButton={false} showTitle={false} showPickPhotoPlaceholder={false} touchable={false} asBackground={false} />
                        }
                        {this.props.customer && <Text style={{ fontSize: 14, color: AppColors.mainText, fontFamily: AppFonts.regular, marginTop: 10 }}>{this.props.customer.name}</Text>}
                        <View style={{ marginTop: 20, marginBottom: 40 }}>
                            <StarRatings maxStars={5}
                                selectedStars={3}
                                onSelect={(rating: number) => { }}
                                selectable={true} withEmptyStars={true}
                                size={40}
                                color={AppColors.ovviumYellow}
                                iconStyle={{ marginHorizontal: 7 }} />
                        </View>
                        <View style={{ borderBottomColor: 'rgba(0,0,0,0.035)', borderBottomWidth: 1, width: '87%' }} />
                        <View style={{ marginTop: 10, alignItems: 'center', justifyContent: 'center' }}>
                            <CircularButtonGroup values={this.props.proposedTips} defaultSelection={this.props.selectedTip ? this.props.selectedTip.toString() : undefined} onChangeValue={this.onChangeValue.bind(this)} />
                            <Button label={msg("bill:tip:otherTip")} onlyText={true} onPress={this.props.goToOtherTip} />
                        </View>
                    </View>
                </ScrollView>
                <View style={{ alignItems: 'center', position: 'absolute', bottom: 0, width: '100%', backgroundColor: 'white', zIndex: 2, height: 80, justifyContent: 'center' }}>
                    <Button label={this.getPendingPaymentText()} containerStyle={billStep2ViewStyle.payButtonContainer} onPress={this.props.goNext} />
                </View>
            </View>
        );
    }

    private async onChangeValue(tip: Tip) {
        var props = this.props;
        setTimeout(function () {
            props.addTip(tip);
        }, 1);
    }

    private getPendingPaymentText() {
        const subtotal = getPendingAmount(this.props.selectedOrders);
        const tip = this.props.selectedTip ? this.props.selectedTip.amount : 0;
        const total = subtotal + tip;
        return msg("actions:pay") + " " + total.toFixed(2) + "€";
    }
}
