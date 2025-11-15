import { BillSplitGrid } from 'app/components/BillSplitGrid/BillSplitGrid';
import { EditableOrdersGrid } from 'app/components/EditableOrdersGrid/EditableOrdersGrid';
import { SelectableOrdersGrid } from 'app/components/EditableOrdersGrid/SelectableOrdersGrid';
import { OrdersGrid } from 'app/components/OrdersGrid/OrdersGrid';
import { Tile } from 'app/components/Tile/Tile';
import { Bill } from 'app/model/Bill';
import { BillSplit } from 'app/model/BillSplit';
import { OrderGroup } from 'app/model/enum/OrderGroup';
import { PaymentMethodType } from 'app/model/enum/PaymentMethodType';
import { InvoiceDate } from 'app/model/InvoiceDate';
import { Order } from 'app/model/Order';
import { Product } from 'app/model/Product';
import { BillService } from 'app/services/BillService';
import { LocationService } from 'app/services/LocationService';
import * as classNames from 'classnames';
import * as React from 'react';
import { Col, Row } from 'react-bootstrap';
import { EditableOrdersGroup } from '../EditableOrdersGroup/EditableOrdersGroup';
import * as style from './style.css';
import { ServiceBuilderLocation } from 'app/model/enum/ServiceBuilderLocation';
import { IssueStatus } from 'app/model/enum/IssueStatus';
import { Customer } from 'app/model/Customer';
import { Loading } from '../Loading/Loading';
import { ConfirmButtons } from '../ConfirmButtons/ConfirmButtons';

interface BillViewProps {
    selectedBill: Bill;
    selectedBillSplit?: BillSplit;
    selectedOrders: Array<Order>;
    showCashAndTicketButtons?: boolean;
    lastInvoiceDate?: InvoiceDate;
    customer: Customer;

    onAddOrder: (product: Product) => void;
    onRemoveOrder: (order: Order) => void;
    onUpdateOrders: (orders: Array<Order>) => void;
    onChangeIssueStatus: (orders: Array<Order>, issueStatus: IssueStatus) => void;
    onSelectBillSplit: (billSplit: BillSplit | undefined) => void;
    onAddOrderToSplit: (order: Order) => void;
    onRemoveOrderFromSplit: (order: Order) => void;
    filterByOrders: (orders: Array<Order>) => void;
    clearOrdersFilters: () => void;
    goToLocations: () => void;
    charge: (paymentMethod: PaymentMethodType) => void;
    printTicket: () => void;
    printKitchenOrders: () => void;
    openCashDrawer: () => void;
    createBillSplit: () => void;
    loadProduct: (producId: string) => Promise<Product>;
}

interface BillViewState {
    optionSelected: 'editBill' | 'editBillSplit' | 'splits' | 'editOrderGroup' | 'selectOrderGroup';
    showPaymentButtons: boolean;
    selectedOrderGroup?: OrderGroup;
    selectedOrders: Array<Order>;
    issueStatus: IssueStatus;
    loading: boolean;
}

export class BillView extends React.Component<BillViewProps, BillViewState> {

    constructor(props) {
        super(props);
        this.state = { optionSelected: 'editBill', showPaymentButtons: false, selectedOrders: [], issueStatus: IssueStatus.ISSUED, loading: false }
    }

    render() {
        if (this.state.loading) {
            return <Loading />
        }
        return <div className="w-100 h-100">
            {window.innerWidth <= 992 && this.state.optionSelected != 'editOrderGroup' &&
                <Row onClick={this.props.goToLocations} className={classNames("w-100",
                    this.props.selectedBill && this.props.selectedBill.locations.length > 1 ? style.shortCurrentLocationJoinedTables : style.shortCurrentLocation,
                    BillService.hasPendingIssueOrders(this.props.selectedBill) ? style.pendingLocationButton : style.fineLocationButton)}>
                    {LocationService.getLocationName(this.props.selectedBill)}
                </Row>
            }
            <Row className="h-100">
                {this.state.optionSelected != 'editOrderGroup' && this.renderBillMenu()}
                {this.state.optionSelected == 'editOrderGroup' && this.renderEditableOrderGroup()}
                <Col lg="4" className={classNames("h-100", style.lightLayout, style.ordersGrid)}>
                    <Row className={classNames(style.wrapper, 'h-100')}>
                        <OrdersGrid bill={this.props.selectedBill}
                            selectedOrders={this.props.selectedOrders}
                            goToLocations={this.props.goToLocations}
                            charge={this.props.charge}
                            lastInvoiceDate={this.props.lastInvoiceDate}
                        />
                    </Row>
                </Col>
            </Row>
        </div>
    }

    private renderBillMenu() {
        var showSelectableOrderGroups = this.state.optionSelected == 'selectOrderGroup';
        var showCashAndTicketButtons = (this.props.showCashAndTicketButtons || window.innerWidth > 992);
        return <Col lg="8" className={"h-100 " + style.lightLayout}>
            <Row className={classNames(showCashAndTicketButtons ? style.mainLayout : style.mainLayoutNoOptionsMultiline, style.wrapper)}>
                {(this.state.optionSelected == 'editBill' || this.state.optionSelected == 'selectOrderGroup') &&
                    <EditableOrdersGrid
                        showSelectable={this.state.optionSelected == 'selectOrderGroup'}
                        selectedOrders={this.props.selectedOrders}
                        onClickMinusProduct={this.props.onRemoveOrder}
                        onClickPlusProduct={this.props.onAddOrder}
                        onChangeCheckedSelectableOrders={selectedOrders => {
                            this.setState({ selectedOrders })
                        }}
                        onClickOrderGroup={orderGroup => {
                            this.onClickOrderGroup(orderGroup)
                        }}
                    />
                }
                {this.state.optionSelected == 'editBillSplit' && this.props.selectedBillSplit &&
                    <div>
                        <SelectableOrdersGrid
                            selectedOrders={this.props.selectedBillSplit.orders}
                            availableOrders={BillService.getPendingPaymentOrdersNotInBillSplit(this.props.selectedBill, this.props.selectedBillSplit)}
                            onAddOrder={this.props.onAddOrderToSplit}
                            onRemoveOrder={this.props.onRemoveOrderFromSplit}
                        />
                    </div>
                }

                {this.state.optionSelected == 'splits' &&
                    <BillSplitGrid
                        splits={this.props.selectedBill ? this.props.selectedBill.splits : []}
                        createBillSplit={this.props.createBillSplit}
                        goToEditBill={this.onModifyBill.bind(this)}
                        onSelectBillSplit={this.onSelectBillSplit.bind(this)}
                    />
                }
            </Row>
            <Row style={{ height: '1%' }}></Row>
            <Row className={classNames(showCashAndTicketButtons ? style.optionsLayout : style.optionsLayoutNoMultiline, style.wrapper)}>
                {!showSelectableOrderGroups && <Tile value="Marcar todo como servido" onClick={() => this.props.onChangeIssueStatus(this.props.selectedOrders, IssueStatus.ISSUED)} className={classNames(style.tileOptionsSecondRow, style.tile)} />}
                {!showSelectableOrderGroups && <Tile value="Marcar bebidas como servidas" onClick={() => {
                    this.props.onChangeIssueStatus(this.props.selectedOrders.filter(o => o.product.serviceBuilderLocation == ServiceBuilderLocation.BAR), IssueStatus.ISSUED)
                }} className={classNames(style.tileOptionsSecondRow, style.tile)} />}

                {showSelectableOrderGroups && <Tile value="Pendiente" selected={this.state.issueStatus == IssueStatus.PENDING}
                    className={classNames(style.issueStatusTile)} onClick={() => this.setState({ issueStatus: IssueStatus.PENDING })} />}
                {showSelectableOrderGroups && <Tile value="En preparación" selected={this.state.issueStatus == IssueStatus.PREPARING}
                    className={classNames(style.issueStatusTile)} onClick={() => this.setState({ issueStatus: IssueStatus.PREPARING })} />}
                {showSelectableOrderGroups && <Tile value="Servido" selected={this.state.issueStatus == IssueStatus.ISSUED}
                    className={classNames(style.issueStatusTile)} onClick={() => this.setState({ issueStatus: IssueStatus.ISSUED })} />}
                {showSelectableOrderGroups && <Tile value="Cancelar" className={style.cancelTileSelectableOrders} onClick={() => {
                    this.setState({ optionSelected: 'editBill', issueStatus: IssueStatus.ISSUED })
                }} />}
                {showSelectableOrderGroups && <Tile value="Aceptar" className={style.acceptTileSelectableOrders} onClick={() => {
                    this.props.onChangeIssueStatus(this.state.selectedOrders, this.state.issueStatus);
                    this.setState({ optionSelected: 'editBill', selectedOrders: [], issueStatus: IssueStatus.ISSUED });
                }
                } />}
                {!showSelectableOrderGroups && <Tile value="Marcar productos como servidos" onClick={this.onSelectOrderGroups.bind(this)} className={classNames(style.tileOptionsSecondRow, style.tile)} />}
                {!showSelectableOrderGroups && showCashAndTicketButtons && <Tile value="Imprimir ticket / Factura" disabled={this.state.optionSelected == 'selectOrderGroup'} onClick={this.props.printTicket} className={classNames(style.tileOptionsSecondRow, style.tile)} />}
                {!showSelectableOrderGroups && showCashAndTicketButtons && <Tile value="Abrir cajón" disabled={this.state.optionSelected == 'selectOrderGroup'} onClick={this.props.openCashDrawer} className={classNames(style.tileOptionsSecondRow, style.tile)} />}
                {!showSelectableOrderGroups && showCashAndTicketButtons && <Tile value="Imprimir comanda para cocina" disabled={this.state.optionSelected == 'selectOrderGroup'} onClick={this.props.printKitchenOrders} className={classNames(style.tileOptionsSecondRow, style.tile)} />}
            </Row>
        </Col>
    }

    private renderEditableOrderGroup() {
        return <Col lg="8" className={"h-100 " + style.lightLayout}>
            <Row className={classNames(style.mainLayoutNoMultilineEditableOrder, style.wrapper)}>
                <EditableOrdersGroup
                    orderGroup={this.state.selectedOrderGroup!}
                    onChangeOrderGroup={orderGroup => this.setState({ selectedOrderGroup: orderGroup })}
                />
            </Row>
            <Row className={style.optionsLayoutNoMultilineEditableOrder}>
                <ConfirmButtons
                        onCancel={() => this.setState({ optionSelected: 'editBill' })}
                        onAccept={() => {
                            this.props.onUpdateOrders(this.state.selectedOrderGroup!.orders)
                            this.setState({ optionSelected: 'editBill' });
                        }}
                    />
            </Row>
        </Col>
    }

    private onModifyBill() {
        this.setState({ optionSelected: 'editBill' });
        this.props.clearOrdersFilters();
        this.props.onSelectBillSplit(undefined);
    }

    private onSelectOrderGroups() {
        if (this.state.optionSelected == 'selectOrderGroup') {
            this.setState({ optionSelected: 'editBill' });
        } else {
            this.setState({ optionSelected: 'selectOrderGroup' });
        }
    }

    private onSelectBillSplit(billSplit: BillSplit) {
        this.setState({ optionSelected: 'editBillSplit' });
        this.props.onSelectBillSplit(billSplit);
    }

    private onClickOrderGroup(orderGroup: OrderGroup) {
        // Load full product group with choices from server on click only
        this.setState({ loading: true })
        this.props.loadProduct(orderGroup.product.id)
            .then(product => {
                orderGroup.product = product;
                this.setState({ selectedOrderGroup: orderGroup, optionSelected: 'editOrderGroup' })
            }).finally(() => this.setState({ loading: false }));
    }
}