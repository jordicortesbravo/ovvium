import SegmentedControl from '@react-native-community/segmented-control';
import React from "react";
import { Animated, Dimensions, Easing, FlatList, ListRenderItemInfo, NativeSyntheticEvent, Platform, RefreshControl, ScrollView, Text, View } from "react-native";
import { Bill } from '../../../model/Bill';
import { Tricks } from '../../../model/enum/Tricks';
import { Invoice } from "../../../model/Invoice";
import { Order } from '../../../model/Order';
import { Product } from '../../../model/Product';
import { InvoicePage } from '../../../model/response/InvoicePage';
import { InvoiceResponse } from '../../../model/response/InvoiceResponse';
import { Tip } from '../../../model/Tip';
import { User } from '../../../model/User';
import { UserBill } from '../../../model/UserBill';
import { getPaymentPendingUserOrders, mapOrdersByProduct, getPendingAmount } from '../../../services/BillService';
import { msg } from '../../../services/LocalizationService';
import { CartState } from '../../../store/State';
import { ArrayUtils } from '../../../util/ArrayUtils';
import { DateUtils } from '../../../util/DateUtils';
import { StringUtils } from '../../../util/StringUtils';
import { AppColors } from "../../styles/layout/AppColors";
import { AndroidTab } from '../AndroidTab/AndroidTab';
import { androidTabBarStyles } from '../AndroidTabBar/style';
import { BillResumeItem } from '../BillResumeItem/BillResumeItem';
import { Button } from "../Button/Button";
import { CartOrderGroupListItem } from '../CartOrderGroupListItem/CartOrderGroupListItem';
import { Header } from '../Header/Header';
import { InvoiceResume } from "../InvoiceResume/InvoiceResume";
import { LoadingView } from '../LoadingView/LoadingView';
import { MenuItem } from '../MenuItem/MenuItem';
import MultifamilyIcon, { IconFamily } from "../MultiFamilyIcon/MultifamilyIcon";
import { billStep1ViewStyle } from './style';
import Swipeable from '../../containers/widgets/Swipeable';
import { AppFonts } from '../../styles/layout/AppFonts';


interface BillStep1ViewProps {
    cart: CartState;
    bill?: Bill;
    userBillList: UserBill[];
    me: User;
    tip?: Tip;
    invoice?: Invoice;
    selectedView?: 'cart' | 'bill' | 'history';
    invoicePage?: InvoicePage;
    onConfirmCart: () => void;
    onAddProductToCart: (product: Product) => void;
    onRemoveProductFromCart: (product: Product) => void;
    onSelectBill: (bill: UserBill | Bill) => void;
    refreshBill: () => void;
    loadInvoiceHistory: (page: number) => void;
    onPressInvoice: (invoice: Invoice) => void;
}

interface BillStep1ViewState {
    refreshing: boolean;
    selectedView: 'cart' | 'bill' | 'history';
    horizontalPivot: Animated.Value;
    ncartOrders: number;
}
export default class BillStep1View extends React.Component<BillStep1ViewProps, BillStep1ViewState> {

    constructor(props: BillStep1ViewProps) {
        super(props);
        //TODO Comprobar si el chart tiene orders y en tal caso, asignar el valor -1 como inicial en horizontalPivot para que cargue primero la pestaña del carrito
        this.state = {refreshing: false, horizontalPivot: new Animated.Value(-1), selectedView: 'cart', ncartOrders: props.cart.orders.length}
    }

    
    UNSAFE_componentWillReceiveProps(newProps: BillStep1ViewProps) {
        var currentNumberCartOrders = newProps.cart.orders.length;
        if(currentNumberCartOrders > 0 && this.state.ncartOrders != currentNumberCartOrders) {
            this.onPressCart();
            this.setState({refreshing: false, ncartOrders: currentNumberCartOrders});
        }
        if(this.state.refreshing) {
            this.setState({refreshing: false});
        } 
    }

    render() {
        var width= Dimensions.get('screen').width;
        var totalAmount = 0;
        this.props.cart.orders.forEach(o => totalAmount += o.price);
        return (
            <View style={{height:'100%'}}>
                <ScrollView style={billStep1ViewStyle.container} refreshControl={<RefreshControl onRefresh={this.refreshBill.bind(this)} refreshing={this.state.refreshing} />}>
                    <Header title={msg("bill:title")} format="big" subtitle={msg("bill:subtitle")}/>
                    {Platform.OS == 'ios' ? this.renderIOSHeader() : this.renderAndroidHeader()}
                    <View style={{flexDirection:'row', height:'100%'}}>
                        {this.renderCart()}
                        {this.renderBill()}
                        {this.renderHistory()}
                    </View>
                </ScrollView>
                <Animated.View style={{width:width, position:'absolute', bottom:0, backgroundColor:'white', zIndex:2, height: 80, justifyContent: 'center', alignItems:'center', transform: [{
                    translateX: this.state.horizontalPivot.interpolate({
                        inputRange: [-1, 0, 1],
                        outputRange: [0, -width, -2*width]
                    })
                }]}}>
                    <Button label={msg("bill:cart:send") + (totalAmount == 0 ? '' : ' (' + totalAmount.toFixed(2) + "€)")} disabled={this.props.cart.orders.length == 0} onPress={() => {
                        this.props.onConfirmCart();
                        setTimeout(()=> {
                            this.onPressBill();
                        }, 500);
                        this.setState({ncartOrders: 0});
                    }} />
                </Animated.View>
            </View>
            
        );
    }

    renderCart() {
        var width= Dimensions.get('screen').width;
        var totalAmount = 0;
        this.props.cart.orders.forEach(o => totalAmount += o.price);
        return  <Animated.View style={{width:width, height: '100%', transform: [{
                    translateX: this.state.horizontalPivot.interpolate({
                        inputRange: [-1, 0, 1],
                        outputRange: [0, -width, -2*width]
                    })
                }]}}>
                    <View style={{padding:20, height: '100%'}}>
                        <Text style={{ fontFamily: AppFonts.bold, fontSize:22,color: AppColors.mainText}}>{msg("bill:cart:label") + " (" + this.props.cart.orders.length + ")"}</Text>
                            {this.props.cart.orders.length == 0 && 
                                    <View style={{marginTop: 60, justifyContent:'center', alignItems:'center'}}>
                                        <MultifamilyIcon style={{marginRight: 20}} family={IconFamily.FEATHER}  name="shopping-cart" size={150} color={AppColors.lightSeparator} />
                                        <Text style={{color: AppColors.lightSeparator, fontFamily: AppFonts.regular, fontSize:16}}>{msg("bill:cart:emptyMessage")}</Text>
                                    </View>
                            }
                            {this.props.cart.orders.length > 0 && 
                                <View>
                                    <FlatList  data={Array.from(mapOrdersByProduct(this.props.cart.orders).values())} renderItem={this.renderOrders.bind(this)} 
                                            style={{marginTop: 10}}
                                            keyExtractor={(item: Array<Order>, index:number) => 'orders_' + index.toString()} />
                                    <View style={billStep1ViewStyle.totalPriceContainer}>
                                        <Text style={billStep1ViewStyle.totalPriceTextLeft}>{msg("bill:total")}</Text>
                                        <Text style={billStep1ViewStyle.totalPriceTextRight}>{totalAmount.toFixed(2) + "€"}</Text>
                                    </View>
                                </View>
                            }
                    </View>
                    <Swipeable message={msg("onboarding:tricks:cart")} id={Tricks.CART}/>
                </Animated.View>
    }

    renderBill() {
        var width= Dimensions.get('screen').width;
        var hasUserUnpayedOrders = getPaymentPendingUserOrders(this.props.bill, [this.props.me]).length > 0;
        return  <Animated.View style={{height: '100%', width:width, transform: [{
                    translateX: this.state.horizontalPivot.interpolate({
                        inputRange: [-1, 0, 1],
                        outputRange: [width, -width, -2*width]
                    })
                }]}}>
                    {this.props.bill && (!this.props.invoice || hasUserUnpayedOrders) && this.props.bill.members.length > 1 && (
                        <BillResumeItem 
                            bill={this.props.bill} me={this.props.me} 
                            ordersByProduct={mapOrdersByProduct(this.props.bill)}
                            tip={this.props.tip}
                            onSelectBill={this.props.onSelectBill}
                        />
                    )}
                    {this.props.invoice &&  !hasUserUnpayedOrders &&
                        <InvoiceResume invoice={this.props.invoice} />
                    }
                    {this.props.bill && (!this.props.invoice || hasUserUnpayedOrders) && 
                        <FlatList data={this.props.userBillList} renderItem={this.renderBillResumeItem} 
                            keyExtractor={(item: UserBill, index:number) => item.user.id + '_' + index.toString()} />
                    }
                    {!this.props.bill && !this.props.invoice &&
                        <View style={{marginTop: 60, justifyContent:'center', alignItems:'center'}}>
                            <MultifamilyIcon style={{marginRight: 20}} family={IconFamily.FEATHER}  name="search" size={120} color={AppColors.lightSeparator} />
                            <Text style={{color: AppColors.lightSeparator, fontFamily: AppFonts.regular, fontSize:16, marginHorizontal:'10%', textAlign:'center'}}>{msg("bill:emptyMessage")}</Text>
                        </View>
                    }
                    {this.state.selectedView == "bill" &&
                        <Swipeable message={msg("onboarding:tricks:billStep1")} id={Tricks.BILL_STEP_1}/>
                    }
                </Animated.View>
    }

    renderHistory() {
        var width= Dimensions.get('screen').width;
        return  <Animated.View style={{height: '100%', width:width, zIndex:2, transform: [{
                    translateX: this.state.horizontalPivot.interpolate({
                        inputRange: [-1, 0, 1],
                        outputRange: [0, width, -2*width]
                    })
                }]}}>
                    {this.props.invoicePage && this.props.invoicePage.content.length > 0 &&
                        <FlatList data={this.props.invoicePage.content} renderItem={this.renderInvoiceResumeItem.bind(this)} 
                            keyExtractor={(item: InvoiceResponse, index:number) => item.id} />
                    }
                     {this.props.invoicePage && this.props.invoicePage.content.length == 0 &&
                        <View>
                            <View style={{marginTop: 60, justifyContent:'center', alignItems:'center'}}>
                                <MultifamilyIcon style={{marginRight: 20}} family={IconFamily.MATERIAL_COMMUNITY}  name="history" size={150} color={AppColors.lightSeparator} />
                                <Text style={{color: AppColors.lightSeparator, fontFamily: AppFonts.regular, fontSize:16}}>{msg("bill:invoice:history:empty")}</Text>
                            </View>
                        </View>
                    }
                    {!this.props.invoicePage &&
                        <View style={{height: '100%', alignItems: 'center', justifyContent: 'center'}}>
                            <LoadingView />
                        </View>
                    }
                </Animated.View>
    }

    renderBillResumeItem = (info: ListRenderItemInfo<UserBill>) => (
        <BillResumeItem 
            bill={this.props.bill!} 
            userBill={info.item} 
            me={this.props.me} 
            tip={this.props.tip}
            ordersByProduct={mapOrdersByProduct(info.item)}
            onSelectBill={this.props.onSelectBill}
            />
    );

    renderInvoiceResumeItem(info: ListRenderItemInfo<InvoiceResponse>) {
        var invoice = info.item;
        var invoiceDate = DateUtils.unwrap(invoice.creationDate);
        var colorId = StringUtils.uuidToInt(invoice.customer.id)
        var size = AppColors.userPlaceholderColors.length-1;
        var colors = AppColors.userPlaceholderColors[colorId%size];
        return  <MenuItem 
                    title={invoice.customer.name}
            subtitle={DateUtils.toISODate(invoiceDate)}
                    iconCapitalText={invoice.customer.name}
                    iconColor={colors.soft}
                    iconTextColor={colors.hard}
                    onPress={() => this.props.onPressInvoice(new Invoice(invoice))}
                    rightElement={<Text style={{fontFamily: AppFonts.bold, color:AppColors.main, marginRight: '7%'}}>{invoice.totalAmount.amount.toFixed(2) + "€"}</Text>} 
                />
    }
        

    renderIOSHeader() {
        var hasUnpayedOrders = this.props.bill && getPendingAmount(this.props.bill) > 0;
        var option1 = msg("bill:cart:label") + " (" + this.props.cart.orders.length + ")";
        var option2 = msg("bill:label") + (hasUnpayedOrders ? " (*)" : "");
        var option3 = msg("bill:invoice:history:label");
        {//@ts-ignore
        }
        return  <SegmentedControl style={{margin: '5%'}} values={[option1 ,option2 , option3]}  fontStyle={{fontFamily: AppFonts.regular}}
            selectedIndex={this.state.selectedView == 'cart' ? 0 : this.state.selectedView == 'bill' ? 1 : 2} onChange={(event: NativeSyntheticEvent<any>) => {
                    var selectedOption = event.nativeEvent.value.toString();
                    selectedOption == option1 ? this.onPressCart() : selectedOption == option2 ? this.onPressBill() : this.onPressHistory();
                }} />
    }

    renderAndroidHeader() {
        return  <View style={androidTabBarStyles.container}>
                    <AndroidTab title={msg("bill:cart:label") + " (" + this.props.cart.orders.length + ")"} 
                        containerStyle={{width:'33.3%'}}
                        selected={this.state.selectedView == 'cart'} onPress={this.onPressCart.bind(this)}/>
                    <AndroidTab title={msg("bill:label")} selected={this.state.selectedView == 'bill'} 
                        containerStyle={{width:'33.3%'}}
                        onPress={this.onPressBill.bind(this)} />
                    <AndroidTab title={msg("bill:invoice:history:label")} selected={this.state.selectedView == 'history'} 
                        containerStyle={{width:'33.3%'}}
                        onPress={this.onPressHistory.bind(this)} />
                </View>
    }

    renderOrders(info: ListRenderItemInfo<Array<Order>>) {
        var orders = info.item;
        if(!orders || orders.length == 0) {
            return <View/>
        }
        var product = ArrayUtils.first(orders).product;
        return  <CartOrderGroupListItem product={product} 
                    orders={orders} onRemoveProductFromCart={this.props.onRemoveProductFromCart} 
                    onAddProductToCart={this.props.onAddProductToCart} />
    }

    refreshBill() {
        this.setState({refreshing: true});
        this.props.refreshBill();
    }

    onPressCart() {
        this.setState({selectedView: 'cart'});
        this.changeHorizontalPivot(-1);
    }

    onPressBill() {
        this.setState({selectedView: 'bill'});
        this.changeHorizontalPivot(0);
    }

    onPressHistory() {
        this.setState({selectedView: 'history'});
        this.changeHorizontalPivot(1);
        this.props.loadInvoiceHistory(0);
    }

    changeHorizontalPivot(value: number) {
        Animated.timing(this.state.horizontalPivot, {
            toValue: value,
            easing: Easing.linear,
            duration: 300,
            useNativeDriver: true
        }).start();
    }
}
