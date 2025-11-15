import { Avatar } from '@material-ui/core';
import { Bill } from 'app/model/Bill';
import { BillStatus } from 'app/model/enum/BillStatus';
import { IssueStatus, getIssueStatusColor, getIssueStatusLabel } from 'app/model/enum/IssueStatus';
import * as classNames from 'classnames';
import * as React from 'react';
import { Nav, NavDropdown } from 'react-bootstrap';
import * as style from './style.css';
import Swiper from 'swiper';
import { LocationService } from 'app/services/LocationService';
import { BillService } from 'app/services/BillService';
import { ProductType } from 'app/model/enum/ProductType';
import { Utils } from 'app/utils/Utils';
import { KitchenOrder } from 'app/utils/ThermalPrinter';
import { ServiceTime, getServiceTimeLabel } from 'app/model/enum/ServiceTime';
import { ChangeIssueStatusDialog } from 'app/components/ChangeIssueStatusDialog/ChangeIssueStatusDialog';
import { Order } from 'app/model/Order';
import PersonIcon from '@material-ui/icons/Person';
import { ServiceBuilderLocation } from 'app/model/enum/ServiceBuilderLocation';

interface TicketsGrid {
    columns: number;
    rows: number;
}

interface KitchenViewProps {
    avatar?: string;
    bills: Array<Bill>;
    onLogout: () => void;
    onChangeIssueStatus: (bill: Bill, orders: Order[], issueStatus: IssueStatus, serviceTime: ServiceTime) => void;
}

interface KitchenViewState {
    issueStatusFilter: IssueStatus | 'ALL';
    dialogOpenStatus: Map<string, boolean>;//La key es serviceTime+billId
    ticketsGrid: TicketsGrid;
}

export class KitchenView extends React.Component<KitchenViewProps, KitchenViewState> {

    constructor(props) {
        super(props);
        var height = window.innerHeight;
        var width = window.innerWidth;

        var columns = width > 768 ? 4 : width > 479 ? 3 : 1;
        var rows = height > 479 && columns > 1 ? 2 : 1;
        this.state = { issueStatusFilter: IssueStatus.PENDING, dialogOpenStatus: new Map<string, boolean>(), ticketsGrid: { columns, rows } };
    }

    componentDidMount() {
        this.initSwiper();
    }

    componentDidUpdate() {
        this.initSwiper();
    }


    render() {
        return <div className="h-100">
            {this.renderTabbar()}
            <div className={classNames(style.swiperContainer, "swiper-container")}>
                <div className="swiper-wrapper">
                    {this.getFilteredBills().map(bill => {
                        return <div key={bill.id} className={classNames(this.state.ticketsGrid.rows == 1 ? style.swiperSlide1Row : style.swiperSlide2Rows, "swiper-slide")}>
                            {this.renderBill(bill)}
                        </div>
                    })}
                </div>
                <div className="swiper-pagination"></div>
            </div>
        </div>
    }

    private renderBill(bill: Bill) {
        var oldestOrderTime = BillService.getOldestPendingOrderTime(bill, ProductType.FOOD);
        var kitchenOrders = this.getKitchenOrderGroup(bill);
        return <div style={{ width: "100%" }}>
            <div className={classNames(style.slideHeader)}>
                <span>{LocationService.getLocationName(bill)}</span>
                <span className={classNames(style.slideHeaderRight)}>Tiempo de espera: {Utils.getElapsedTime(oldestOrderTime)}</span>
            </div>
            <div>
                {this.renderOrders(bill, ServiceTime.SOONER, kitchenOrders.get(ServiceTime.SOONER))}
                {this.renderOrders(bill, ServiceTime.STARTER, kitchenOrders.get(ServiceTime.STARTER))}
                {this.renderOrders(bill, ServiceTime.FIRST_COURSE, kitchenOrders.get(ServiceTime.FIRST_COURSE))}
                {this.renderOrders(bill, ServiceTime.SECOND_COURSE, kitchenOrders.get(ServiceTime.SECOND_COURSE))}
                {this.renderOrders(bill, ServiceTime.DESSERT, kitchenOrders.get(ServiceTime.DESSERT))}
                {this.renderOrders(bill, ServiceTime.OTHER, kitchenOrders.get(ServiceTime.OTHER))}
            </div>
        </div>
    }

    private renderOrders(bill: Bill, serviceTime: ServiceTime, kitchenOrders: KitchenOrder[] | undefined) {
        if (kitchenOrders) {
            var orders = kitchenOrders.map(order => {
                return <div key={order.order?.id} style={{ marginTop: '3px' }}>
                    <span>{order.productName}</span>
                    {order.notes && <div style={{ fontStyle: "italic", color: "var(--linkColor)", marginLeft: "20px" }}>{"- " + order.notes}</div>}
                </div>
            });

            var dialogOpen = this.state.dialogOpenStatus.get(serviceTime + bill.id) == true;
            var firstOrderIssueStatus = this.getIssueStatusOfOrders(kitchenOrders);

            var issueStatusColor = firstOrderIssueStatus ? getIssueStatusColor(firstOrderIssueStatus) : "var(--linkColor)";
            var issueStatusText = firstOrderIssueStatus ? getIssueStatusLabel(firstOrderIssueStatus) : "Cambiar estado";
            return <div style={{ marginTop: "10px", background: "var(--terciaryBackground)", padding: "10px", fontSize: '11px' }}>
                <a href="#" onClick={() => this.onToggleDialog(bill, serviceTime, true)}><span style={{ color: 'white', padding: '5px', borderRadius: "6px", backgroundColor: issueStatusColor, float: 'right' }}>{issueStatusText}</span></a>
                <div style={{ fontStyle: 'italic', color: 'var(--ovviumYellow)', marginBottom: "15px" }}>{getServiceTimeLabel(serviceTime).toUpperCase()}</div>
                {orders}
                <ChangeIssueStatusDialog
                    location={LocationService.getLocationName(bill)}
                    serviceTime={serviceTime}
                    open={dialogOpen}
                    onCancel={() => this.onToggleDialog(bill, serviceTime, false)}
                    onChangeIssueStatus={(issueStatus: IssueStatus) => {
                        this.props.onChangeIssueStatus(bill, kitchenOrders.map(ko => ko.order!), issueStatus, serviceTime);
                        this.onToggleDialog(bill, serviceTime, false);
                    }} />
            </div>;
        }
        return;
    }

    private getIssueStatusOfOrders(kitchenOrders: KitchenOrder[]): IssueStatus | undefined {
        if (kitchenOrders.length === 0)
            return undefined;
        let firstKitchenOrder = kitchenOrders[0]!;
        if (firstKitchenOrder.orderGroupChoice) {
            return firstKitchenOrder.orderGroupChoice!.issueStatus;
        }
        return kitchenOrders[0].order?.issueStatus;
    }

    private onToggleDialog(bill: Bill, serviceTime: ServiceTime, status: boolean) {
        var dialogOpenStatus = this.state.dialogOpenStatus;
        dialogOpenStatus.set(serviceTime + bill.id, status);
        this.setState({ dialogOpenStatus })
    }


    private renderTabbar() {

        return (
            <div className="container-fluid" style={{ float: 'right' }}>
                <Nav className="ml-auto">
                    <NavDropdown title={this.avatar(this.props.avatar)} id="nav-dropdown" className={style.avatar}>
                        <NavDropdown.Item href="/config">Modo bar</NavDropdown.Item>
                        <NavDropdown.Item eventKey="logout" onClick={() => this.props.onLogout()}>Cerrar sesión</NavDropdown.Item>
                    </NavDropdown>
                </Nav>
            </div>
        )
    }

    private avatar(avatar: string | undefined) {
        return (
            <Avatar src={avatar}>
                {!avatar && <PersonIcon />}
            </Avatar>
        )
    }

    private getFilteredBills() {
        var orders = this.props.bills
            .filter(b => b.billStatus == BillStatus.OPEN)
            .filter(b => b.orders.filter(o => o.product.serviceBuilderLocation == ServiceBuilderLocation.KITCHEN || o.product.type == ProductType.GROUP).length > 0);

        return orders;
    }

    getKitchenOrderGroup(bill: Bill) {
        var map = new Map<string, KitchenOrder[]>();
        bill.orders.filter(o => o.product.type == ProductType.FOOD || o.product.type == ProductType.GROUP).forEach(o => {
            if (o.product.type == ProductType.GROUP && o.groupChoices) {
                o.groupChoices.forEach(groupChoice => {
                    var serviceTime = groupChoice.serviceTime;
                    var kitchenOrder = {
                        order: o,
                        orderGroupChoice: groupChoice,
                        productName: groupChoice.product.name,
                        notes: groupChoice.notes ? groupChoice.notes : (o.notes ? o.notes : "")
                    } as KitchenOrder;
                    if (map.has(serviceTime)) {
                        map.get(serviceTime)!.push(kitchenOrder);
                    } else {
                        map.set(serviceTime, [kitchenOrder]);
                    }
                })
            } else {
                var serviceTime = o.serviceTime!;
                var kitchenOrder = {
                    order: o,
                    productName: o.product.name,
                    notes: o.notes ? o.notes : ""
                } as KitchenOrder;
                if (map.has(serviceTime)) {
                    map.get(serviceTime)!.push(kitchenOrder);
                } else {
                    map.set(serviceTime, [kitchenOrder]);
                }
            }
        });
        return map;
    }

    private initSwiper() {
        new Swiper('.swiper-container', {
            slidesPerView: this.state.ticketsGrid.columns,
            slidesPerColumn: this.state.ticketsGrid.rows,
            spaceBetween: 10,
            pagination: {
                el: '.swiper-pagination',
            },
        })
    }

}