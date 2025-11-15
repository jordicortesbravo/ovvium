import React from "react";
import { ScrollView, View, FlatList, ListRenderItemInfo, Text, Image, Platform } from "react-native";
import { Bill } from '../../../model/Bill';
import { PaymentMethod } from '../../../model/PaymentMethod';
import { msg, getLocalization } from '../../../services/LocalizationService';
import { AppColors } from '../../styles/layout/AppColors';
import { Header } from '../Header/Header';
import { PaymentMethodForm } from '../PaymentMethodForm/PaymentMethodForm';
import { Invoice } from "../../../model/Invoice";
import { mapOrdersByProduct, mapOrdersByProductInInvoice } from "../../../services/BillService";
import { OrderInvoice } from "../../../model/OrderInvoice";
import { ArrayUtils } from "../../../util/ArrayUtils";
import { invoiceResumeStyle } from "./style";
import { DateUtils } from "../../../util/DateUtils";

interface InvoiceResumeProps {
    invoice?: Invoice;
}

export class InvoiceResume extends React.Component<InvoiceResumeProps> {

    constructor(props: InvoiceResumeProps) {
        super(props);
    }

    render() {
        if(!this.props.invoice) {
            return <></>;
        }
        var basePrice = 0;
        this.props.invoice.orders.forEach(order => basePrice += order.basePrice);
        var tax = this.props.invoice.totalAmount - this.props.invoice.tipAmount - basePrice;
        var tip = this.props.invoice.tipAmount;
        var invoiceDate = DateUtils.unwrap(this.props.invoice.creationDate);
        var invoiceDateFormatted = DateUtils.toISODate(invoiceDate);
        return  <View style={{backgroundColor: AppColors.veryLightGray, marginHorizontal:20, marginTop: 20, borderRadius: 10}}>
                    <View style={{paddingHorizontal: 30, marginVertical: 10}}>
                        <Text style={invoiceResumeStyle.title}>{msg("bill:invoice:label")}</Text>
                        <Text style={invoiceResumeStyle.resumeText}>{msg("bill:invoice:issuer:name") + ": " + this.props.invoice.customer!.name}</Text>
                        <Text style={invoiceResumeStyle.resumeText}>{msg("bill:invoice:issuer:cif") + ": " + this.props.invoice.customer!.cif}</Text>
                        <Text style={invoiceResumeStyle.resumeText}>{msg("bill:invoice:id") + ": " + this.props.invoice.invoiceNumber}</Text>
                        <Text style={invoiceResumeStyle.resumeText}>{msg("bill:invoice:date") + ": " + invoiceDateFormatted}</Text>
                    </View>
                    <FlatList data={Array.from(mapOrdersByProductInInvoice(this.props.invoice.orders).values())} renderItem={this.renderOrders} 
                            style={{marginTop: 10}}
                            keyExtractor={(item: Array<OrderInvoice>, index:number) => 'orders_' + index.toString()} />
                        <View style={invoiceResumeStyle.totalPriceContainer}>
                            <Text style={[invoiceResumeStyle.totalPriceTextLeft, {fontSize: 15}]}>{msg("bill:invoice:base")}</Text>
                            <Text style={[invoiceResumeStyle.totalPriceTextRight, {fontSize: 15}]}>{basePrice.toFixed(2)+ "€"}</Text>
                        </View>
                        <View style={invoiceResumeStyle.totalPriceContainer}>
                            <Text style={[invoiceResumeStyle.totalPriceTextLeft, {fontSize: 15}]}>{msg("bill:invoice:tax")}</Text>
                            <Text style={[invoiceResumeStyle.totalPriceTextRight, {fontSize: 15}]}>{tax.toFixed(2) + "€"}</Text>
                        </View>
                        <View style={invoiceResumeStyle.totalPriceContainer}>
                            <Text style={[invoiceResumeStyle.totalPriceTextLeft, {fontSize: 15}]}>{msg("bill:invoice:tip")}</Text>
                            <Text style={[invoiceResumeStyle.totalPriceTextRight, {fontSize: 15}]}>{tip.toFixed(2)+ "€"}</Text>
                        </View>
                        <View style={[invoiceResumeStyle.totalPriceContainer, {marginTop: 20, marginBottom:50}]}>
                            <Text style={invoiceResumeStyle.totalPriceTextLeft}>{msg("bill:total")}</Text>
                            <Text style={invoiceResumeStyle.totalPriceTextRight}>{this.props.invoice.totalAmount.toFixed(2) + "€"}</Text>
                        </View>
                </View>
    }

    renderOrders(info: ListRenderItemInfo<Array<OrderInvoice>>) {
        var orders = info.item;
        if(!orders || orders.length == 0) {
            return <View/>
        }
        var product = getLocalization(ArrayUtils.first(orders).productName);
        var price = ArrayUtils.first(orders).price;
        var norders = orders.length;
        return <View>
                    <View style={invoiceResumeStyle.card}>
                        <Text style={invoiceResumeStyle.numberOfItemsText}>{norders + " x "}</Text>
                        <View style={invoiceResumeStyle.descriptionContainer}>
                            <View style={{ marginBottom: 3 }}>
                                <Text style={invoiceResumeStyle.titleText}>{product}</Text>
                            </View>
                            <View>
                                <Text style={invoiceResumeStyle.descriptionText}>
                                {msg("products:unitPrice") + ": " + price.toFixed(2)+'€'}
                                </Text>
                            </View>
                        </View>
                        <View style={{width:'17%',flexDirection: 'column', alignItems:'flex-end'}}>
                            <View style={invoiceResumeStyle.priceContainer} >
                                <Text style={invoiceResumeStyle.priceText}>{(norders * price).toFixed(2)+'€'}</Text>
                            </View>
                        </View> 
                    </View>
                </View>
    }


}
