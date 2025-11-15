import React from "react";
import { Animated, Easing, Image, ScrollView, Text, TouchableOpacity, View, Vibration, Dimensions, Platform } from "react-native";
import EvilIcons from "react-native-vector-icons/EvilIcons";
import { Customer } from '../../../model/Customer';
import { CreditCardType } from '../../../model/enum/CreditCardType';
import { PaymentStatus } from '../../../model/enum/PaymentStatus';
import { Order } from '../../../model/Order';
import { PaymentMethod } from '../../../model/PaymentMethod';
import { Tip } from '../../../model/Tip';
import { User } from '../../../model/User';
import { getTotalAmount } from '../../../services/BillService';
import { msg } from '../../../services/LocalizationService';
import { errorMessage } from '../../../util/WidgetUtils';
import { AppColors } from '../../styles/layout/AppColors';
import { billStep2ViewStyle } from '../BillStep2View/style';
import { Button } from '../Button/Button';
import { Header } from '../Header/Header';
import MultifamilyIcon, { IconFamily } from '../MultiFamilyIcon/MultifamilyIcon';
import { billStep5ViewStyle } from './style';
import { LoadingView } from "../LoadingView/LoadingView";
import { AppFonts } from "../../styles/layout/AppFonts";

interface BillStep5ViewProps {
    customer: Customer;
    paymentMethod?: PaymentMethod;
    usersToPay: User[];
    ordersToPay: Order[];
    tip: Tip;
    showIndicator: boolean;
    paymentStatus?: PaymentStatus;
    error?: any;
    goToPickPaymentMethod: () => void;
    goBack: () => void;
    goToInvoice: () => void;
    pay: () => void;
}

interface BillStep5ViewState {
    paymentOk: Animated.Value;
    paymentOkOpacityViewPosition: Animated.Value;
    paymentKo: Animated.Value;
    showPayButton: Animated.Value;
    paymentKoValue: number;
}

export default class BillStep5View extends React.Component<BillStep5ViewProps, BillStep5ViewState> {

    constructor(props:BillStep5ViewProps) {
        super(props);
        this.state = {paymentOk: new Animated.Value(0), paymentOkOpacityViewPosition: new Animated.Value(0),paymentKo: new Animated.Value(0),showPayButton: new Animated.Value(1), paymentKoValue: 0}
        this.state.paymentKo.addListener(({value}) => this.setState({paymentKoValue: value}));
    }

    UNSAFE_componentWillReceiveProps(props: BillStep5ViewProps) {
        if(props.paymentStatus) {
            if(props.paymentStatus == PaymentStatus.OK) {
                this.showPaymentOkMessage();
            } else if(props.paymentStatus == PaymentStatus.KO) {
                this.showPaymentKoMessage();
            }
        } 
    }

    render() {
        const subtotal = getTotalAmount(this.props.ordersToPay);
        const tip = this.props.tip ? this.props.tip.amount : 0;
        const total = subtotal + tip;
        var subtotalMessage = msg("bill:payment:subtotal").toString();
        var tipMessage = msg("bill:payment:tip").toString();
        var totalMessage = msg("bill:payment:total").toString();
        var entityMessage = msg("bill:payment:entity").toString();
        var countryMessage = msg("bill:payment:country:title").toString();
        var creditCardMessage = this.props.paymentMethod ? this.renderCreditCardType(this.props.paymentMethod) : undefined;
        var billMessages = msg("bill:payment:bills").toString();
        var width = Dimensions.get('screen').width;

        return (
            <View style={billStep5ViewStyle.container}>
                <ScrollView>
                    <Header goBack={this.props.goBack} goBackTitle={msg("actions:back")}  format="big" title={msg("bill:payment:confirmation")} subtitle={msg("bill:payment:resume")}/>

                    {!this.props.paymentMethod &&
                        <View style={{height: '100%', alignItems: 'center', justifyContent: 'center'}}>
                            <LoadingView />
                        </View>
                    } 
                    {this.props.paymentMethod &&
                        <View>
                            <TouchableOpacity style={[billStep5ViewStyle.dataCardContainer, {paddingTop: 30}]} onPress={this.props.goToPickPaymentMethod}>
                                <View style={billStep5ViewStyle.cardLeftContainer}>
                                    {this.renderCreditCardBrand(this.props.paymentMethod)}
                                </View>
                                <View style={billStep5ViewStyle.cardRightContainer}>
                                    <Text style={billStep5ViewStyle.cardDataRight}>{creditCardMessage}</Text>
                                    <Text style={billStep5ViewStyle.cardDataRight}>{this.props.paymentMethod.cardNumber}</Text>
                                </View>
                                <View style={{right: 10, height:35, paddingTop: 20, position:'absolute', justifyContent: 'center'}}>
                                    <MultifamilyIcon family={IconFamily.MATERIAL_COMMUNITY} name="chevron-right" size={22} color={'#D1D1D6'}/>
                                </View>
                            </TouchableOpacity>
                            <View style={[billStep5ViewStyle.dataCardContainer, {minHeight:70}]}>
                                <View style={billStep5ViewStyle.cardLeftContainer}>
                                    <Text style={billStep5ViewStyle.cardDataLeft}>{entityMessage}</Text>
                                </View>
                                <View style={billStep5ViewStyle.cardRightContainer}>
                                    <Text style={billStep5ViewStyle.cardDataRight}>{this.props.customer ? this.props.customer.name : ''}</Text>
                                </View>
                            </View>
                            <View style={[billStep5ViewStyle.dataCardContainer, {minHeight:60, marginTop: -20}]}>
                                <View style={billStep5ViewStyle.cardLeftContainer}>
                                    <Text style={billStep5ViewStyle.cardDataLeft}>{countryMessage}</Text>
                                </View>
                                <View style={billStep5ViewStyle.cardRightContainer}>
                                    <Text style={billStep5ViewStyle.cardDataRight}>{msg("bill:payment:country:spain")}</Text>
                                </View>
                            </View>
                            <View style={billStep5ViewStyle.dataCardContainer}>
                                <View style={billStep5ViewStyle.cardLeftContainer}>
                                    <Text style={billStep5ViewStyle.cardDataLeft}>{billMessages}</Text>
                                </View>
                                <View style={billStep5ViewStyle.cardRightContainer}>
                                    <Text style={[billStep5ViewStyle.cardDataRight, {width:'90%'}]}>{this.getSelectedUserNames()}</Text>
                                </View>
                            </View>
                            <View style={[billStep5ViewStyle.dataCardContainer, {minHeight:100}]}>
                                <View style={billStep5ViewStyle.cardLeftContainer}></View>
                                <View style={[billStep5ViewStyle.cardRightContainer, {flexDirection:'row'}]}>
                                    <View>
                                        <Text style={billStep5ViewStyle.cardDataLeft}>{subtotalMessage}</Text>
                                        <Text style={[billStep5ViewStyle.cardDataLeft, {marginTop:5}]}>{tipMessage}</Text>
                                        <Text style={[billStep5ViewStyle.cardDataLeft, {marginTop:20}, {fontFamily: AppFonts.bold, color:AppColors.secondaryText}]}>{totalMessage}</Text>
                                    </View>
                                    <View style={{alignItems:'flex-end', width:'50%'}}>
                                        <Text style={billStep5ViewStyle.cardDataRight}>{getTotalAmount(this.props.ordersToPay).toFixed(2)+'€'}</Text>
                                        <Text style={[billStep5ViewStyle.cardDataRight, {marginTop:5}]}>{tip.toFixed(2)+'€'}</Text>
                                        <Text style={[billStep5ViewStyle.cardDataRight, {marginTop:17, fontSize:20},{fontFamily: AppFonts.bold}]}>{total.toFixed(2)+'€'}</Text>
                                    </View>
                                </View>
                            </View>
                            <View style={{height: 100, zIndex: 3}}>
                                <Animated.View style={{alignItems:'center', marginTop: 25, opacity: this.state.paymentKo}}>
                                    <EvilIcons size={70} color={AppColors.red} name="exclamation"/>
                                    <Text style={{fontSize:14, fontFamily:AppFonts.regular,marginTop:5}}>{msg("bill:payment:ko")}</Text>
                                    <Text style={{fontSize:14, fontFamily:AppFonts.regular}}>{errorMessage(this.props.error)}</Text>
                                </Animated.View>
                            </View>
                        </View>
                    }
                </ScrollView>
                
                
                <Animated.View style={{alignItems: 'center', position:'absolute', bottom:0, width: '100%', backgroundColor:'white', zIndex:2, height: 80, justifyContent: 'center', opacity: this.state.showPayButton}}>
                    <Button label={msg("actions:pay") + " " + total.toFixed(2)+'€'} containerStyle={billStep2ViewStyle.payButtonContainer} onPress={this.pay.bind(this)} showIndicator={this.props.paymentStatus == PaymentStatus.PROCESSING} />
                </Animated.View>
            
                <Animated.View style={{height: "70%", width:"100%", position:'absolute', top:0, opacity: this.state.paymentOk, transform: [{
                    translateX: this.state.paymentOk.interpolate({
                    inputRange: [0, 1],
                    outputRange: [width, 0]
                    })
                }]}}>
                    <View style={{height:'100%', opacity: 0.8 ,backgroundColor: '#000', width:"100%"}}></View>
                </Animated.View>
                <Animated.View style={{height: '30%', justifyContent: 'center', alignItems: 'center', opacity: this.state.paymentOk}}>
                    <Button label={msg("bill:invoice:label")} onPress={this.props.goToInvoice} onlyText={true} containerStyle={{position:'absolute', right:15, top: Platform.OS == 'ios' ? 25 : 7}} textStyle={{color: AppColors.ovviumYellow}}/>
                    <Text style={{fontFamily: AppFonts.bold, fontSize: 22, color:AppColors.ovviumBlue, marginBottom:40, paddingTop: 20}}>{msg("bill:payment:thanks")}</Text>
                    <EvilIcons size={70} color={AppColors.main} name="check"/>
                    <Text style={{fontSize:14, fontFamily:AppFonts.medium,marginTop:10, color: AppColors.mainText}}>{msg("bill:payment:ok")}</Text>
                </Animated.View>
                
            </View>
        );
    }

    pay() {
        this.hidePaymentKoMessage();
        this.props.pay();
    }

    getSelectedUserNames() {
        var s = "";
        for(var i in this.props.usersToPay) {
            s += this.props.usersToPay[i].name;
            s+= ", "; 
        }
        return s.substr(0, s.length-2);
    }

    showPaymentOkMessage() {
        Animated.parallel([
            Animated.timing(this.state.paymentOk, {
                toValue:1,
                easing: Easing.linear,
                duration: 300,
                useNativeDriver: true
              }),
              Animated.timing(this.state.showPayButton, {
                toValue:0,
                easing: Easing.linear,
                duration: 100,
                useNativeDriver: true
              }),
              Animated.timing(this.state.paymentOkOpacityViewPosition, {
                toValue:1,
                easing: Easing.linear,
                duration: 1,
                useNativeDriver: true
              }), 
        ]).start();
        Vibration.vibrate(400);
    }
    
    showPaymentKoMessage() {
        Animated.timing(this.state.paymentKo, {
            toValue:1,
            easing: Easing.linear,
            duration: 300,
            useNativeDriver: true
          }).start();
    }
    
    hidePaymentKoMessage() {
        Animated.timing(this.state.paymentKo, {
            toValue:0,
            easing: Easing.linear,
            duration: 10,
            useNativeDriver: true
          }).start();
    }

    renderCreditCardType(paymentMethod: PaymentMethod) {
        return paymentMethod.cardType == 'CREDIT' ? msg("bill:payment:creditCard") : paymentMethod.cardType == 'DEBIT' ? msg("bill:payment:debitCard") : ""
    }

    renderCreditCardBrand(paymentMethod: PaymentMethod) {
        if(paymentMethod && paymentMethod.brand) {
            try {
                switch(paymentMethod.brand) {
                    case CreditCardType.VISA:
                    case CreditCardType.VISA_ELECTRON:
                        return <Image source={require('../../../../assets/images/icons/credit-cards/visa.png')} style={{width:50, height: 15, marginTop:5}} />
                    case CreditCardType.MAESTRO:
                        return <Image source={require('../../../../assets/images/icons/credit-cards/maestro.png')} style={{width:45, height: 35}} />
                    case CreditCardType.MASTERCARD:
                        return <Image source={require('../../../../assets/images/icons/credit-cards/mastercard.png')} style={{width:45, height: 35}} />
                    case CreditCardType.AMEX:
                        return <Image source={require('../../../../assets/images/icons/credit-cards/amex.png')} style={{width:45, height: 35}} />
                    case CreditCardType.DISCOVER:
                        return <Image source={require('../../../../assets/images/icons/credit-cards/discover.png')} style={{width:45, height: 25}} />
                    case CreditCardType.DINERS:
                        return <Image source={require('../../../../assets/images/icons/credit-cards/diners.png')} style={{width:45, height: 25}} />
                    case CreditCardType.JCB:
                        return <Image source={require('../../../../assets/images/icons/credit-cards/jcb.png')} style={{width:45, height: 25}} />
                }
            } catch(Error) {}
        }
        return undefined;
        
    }
}
