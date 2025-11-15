import React from "react";
import { Platform, Text, View } from 'react-native';
import { connect } from 'react-redux';
import { AnyAction, bindActionCreators, Dispatch } from 'redux';
import { refreshBillActionCreator } from '../../../actions/BillActions';
import { Bill } from '../../../model/Bill';
import { Customer } from '../../../model/Customer';
import { User } from '../../../model/User';
import { AppState } from '../../../store/State';
import MultifamilyIcon, { IconFamily } from '../../components/MultiFamilyIcon/MultifamilyIcon';
import { AppColors } from '../../styles/layout/AppColors';
import { AppFonts } from "../../styles/layout/AppFonts";

interface CartIconProps {
    norders: number;
    color?: string;
    bill?: Bill;
    me: User;
    refreshBill: (user: User) => void;
}

class CartIcon extends React.Component<CartIconProps> {
  render() {
    return (
        <View onTouchStart={() => this.refreshBill()}>
            <MultifamilyIcon family={IconFamily.FEATHER}  name="shopping-cart" size={25} color={this.props.color} />
                {this.props.norders > 0 ?
                    <View style={{
                        position: "absolute",
                        marginLeft: Platform.OS == 'ios' ? 21 : 25,
                        width: 20,
                        height: 20,
                        justifyContent: 'center',
                        alignItems: 'center',
                        borderRadius: 10,
                        backgroundColor: AppColors.red,
                        zIndex: 2}}>
                        <Text style={{color:AppColors.white, fontFamily: AppFonts.bold}}>{this.props.norders}</Text>
                    </View> : <View/>
                } 
        </View>
    );
  }

  refreshBill() {
    if(this.props.bill) {
        this.props.refreshBill(this.props.me)
    }
  }
}

function mapStateToProps(state: AppState): CartIconProps {
    return {
        norders: state.cartState.orders.length,
        bill: state.billState.bill,
        me: state.sessionState.user
    } as CartIconProps;
}

function mapDispatchToProps(dispatch: Dispatch<AnyAction>) {
    return bindActionCreators(
      {
        refreshBill: refreshBillActionCreator
      },
      dispatch
    );
  }

export default connect(
    mapStateToProps,
    mapDispatchToProps
  )(CartIcon);
