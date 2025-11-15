import { KitchenView } from 'app/components/Kitchen/KitchenView';
import { AppState } from 'app/store/AppState';
import * as React from 'react';
import { connect } from 'react-redux';
import { AnyAction, Dispatch } from 'redux';
import { Bill } from 'app/model/Bill';
import { Order } from 'app/model/Order';
import { Customer } from 'app/model/Customer';
import { IssueStatus } from 'app/model/enum/IssueStatus';
import { updateOrdersActionCreator } from 'app/actions/BillActions';
import { onLogoutActionCreator } from 'app/actions/UserActions';
import { ServiceTime } from 'app/model/enum/ServiceTime';
import { ProductType } from 'app/model/enum/ProductType';
import { BaseContainer, BaseContainerState, baseMapDispatchToProps } from 'app/containers/BaseContainer';
import { BaseContainerProps, baseMapStateToProps } from './../BaseContainer';
import { AppRoute } from '../Router/AppRoute';
import { ErrorSnackbar } from 'app/components/ErrorSnackbar/ErrorSnackbar';

interface KitchenContainerProps extends BaseContainerProps {
    customer: Customer;
    bills: Array<Bill>;
    avatar?: string;
    logout: () => void;
    updateOrders: (customer: Customer, bill: Bill, orders: Array<Order>) => void;
}

interface KitchenContainerState extends BaseContainerState {

}

class KitchenContainer extends BaseContainer<KitchenContainerProps, KitchenContainerState> {

    constructor(props) {
        super(props, {
            errorDialogOpened: false
        });
    }

    render() {
        return <>
            <KitchenView
                bills={this.props.bills}
                avatar={this.props.avatar}
                onLogout={this.props.logout}
                onChangeIssueStatus={this.changeIssueStatus.bind(this)}
            />
            <ErrorSnackbar show={this.state.errorDialogOpened} error={this.props.error} onClose={this.onClearError.bind(this)} />
        </>
    }

    changeIssueStatus(bill: Bill, orders: Order[], issueStatus: IssueStatus, serviceTime: ServiceTime) {
        orders.forEach(o => {
            if (o.product.type == ProductType.GROUP) {
                o.groupChoices?.filter(gc => gc.serviceTime === serviceTime)?.forEach(gc => gc.issueStatus = issueStatus)
            } else {
                o.issueStatus = issueStatus
            }
        });
        this.props.updateOrders(this.props.customer, bill, orders);
    }

}

function mapStateToProps(state: AppState): KitchenContainerProps {
    return baseMapStateToProps(state, {
        bills: state.billState.bills,
        customer: state.billState.customer,
        avatar: state.sessionState.user!.imageUri,
        route: AppRoute.KITCHEN
    } as KitchenContainerProps);
}

function mapDispatchToProps(dispatch: Dispatch<AnyAction>) {
    return baseMapDispatchToProps(dispatch,
        {
            updateOrders: updateOrdersActionCreator,
            logout: onLogoutActionCreator
        }
    );
}

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(KitchenContainer);
