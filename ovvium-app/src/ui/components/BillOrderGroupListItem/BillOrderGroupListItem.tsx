import React from 'react';
import { Text, View, TextStyle } from 'react-native';
import { billOrderGroupListItemStyle } from './style';
import { Order } from '../../../model/Order';
import { msg } from '../../../services/LocalizationService';
import { getTotalAmount, getPendingAmount } from '../../../services/BillService';
import { Product } from '../../../model/Product';
import { AppColors } from '../../styles/layout/AppColors';
import { AppFonts } from '../../styles/layout/AppFonts';

interface BillOrderGroupListItemProps {
    orders: Array<Order>;
    product: Product;
}


export class BillOrderGroupListItem extends React.Component<BillOrderGroupListItemProps> {
    render() {
      return (
        <View>
            <View style={billOrderGroupListItemStyle.card}>
              <Text style={billOrderGroupListItemStyle.numberOfItemsText}>{this.props.orders.length + " x "}</Text>
              <View style={billOrderGroupListItemStyle.descriptionContainer}>
                <View style={{ marginBottom: 3 }}>
                    <Text style={billOrderGroupListItemStyle.titleText}>{this.props.product.name}</Text>
                  </View>
                  <View>
                    <Text style={billOrderGroupListItemStyle.descriptionText}>
                      {msg("products:unitPrice") + ": " + this.props.product.price.toFixed(2)+'€'}
                    </Text>
                  </View>
              </View>
              <View style={{width:'17%',flexDirection: 'column', alignItems:'flex-end'}}>
                  <View style={billOrderGroupListItemStyle.priceContainer} >
                    <Text style={billOrderGroupListItemStyle.priceText}>{getTotalAmount(this.props.orders).toFixed(2)+'€'}</Text>
                  </View>
                  <View style={billOrderGroupListItemStyle.secondaryPriceContainer}>
                      <Text style={[billOrderGroupListItemStyle.secondaryPriceText, getPendingAmount(this.props.orders) == 0 ? {color: AppColors.funnyGreen, fontFamily: AppFonts.bold} : {color: AppColors.red}]}>{this.getPendingPaymentText(getPendingAmount(this.props.orders))}</Text>
                  </View>
              </View> 
            </View>
          </View>
      )
    }

    getPendingPaymentText(pendingPayment: number): string {
      return pendingPayment == 0 ? msg("bill:paid") : pendingPayment.toFixed(2)+'€';
    }
}