import { browserHistory } from 'app/App';
import { BillView } from 'app/components/Bill/BillView';
import { AppRoute } from 'app/containers/Router/AppRoute';
import { Bill } from 'app/model/Bill';
import { BillSplit } from 'app/model/BillSplit';
import { Customer } from 'app/model/Customer';
import { IssueStatus } from 'app/model/enum/IssueStatus';
import { OrderPaymentStatusFilter } from 'app/model/enum/OrderPaymentStatusFilter';
import { PaymentMethodType } from 'app/model/enum/PaymentMethodType';
import { Invoice } from 'app/model/Invoice';
import { Order } from 'app/model/Order';
import { Product } from 'app/model/Product';
import { User } from 'app/model/User';
import { AppState } from 'app/reducers/RootReducer';
import { BillService } from 'app/services/BillService';
import { SerializationUtils } from 'app/utils/SerializationUtils';
import { KitchenOrder, openCashDrawer, printBill, PrinterInterface, printKitchenOrders } from 'app/utils/ThermalPrinter';
import { Utils } from 'app/utils/Utils';
import * as React from 'react';
import { connect } from 'react-redux';
import { AnyAction, Dispatch } from 'redux';
import { addOrderToSelectedBillSplitActionCreator, chargeActionCreator, createBillSplitActionCreator, createInvoiceDraftActionCreator, createOrderActionCreator, removeOrderActionCreator, removeOrderFromSelectedBillSplitActionCreator, selectBillSplitActionCreator, updateOrdersActionCreator } from '../../actions/BillActions';
import { getLastInvoiceDateCreator } from 'app/actions/InvoiceActions';
import { InvoiceDate } from 'app/model/InvoiceDate';
import { InvoiceDateClosedDialog } from 'app/components/Dialog/InvoiceDateClosedDialog';
import { BaseContainer, BaseContainerProps, BaseContainerState } from '../BaseContainer';
import { CreateOrderRequest } from './../../model/request/CreateOrderRequest';
import { loadFullProduct } from 'app/actions/ProductActions';
import { ProductType } from 'app/model/enum/ProductType';
import { loadPrinterState } from '../Configuration/PrintersContainer';
import { baseMapStateToProps } from './../BaseContainer';
import { baseMapDispatchToProps } from 'app/containers/BaseContainer';
import { ErrorSnackbar } from 'app/components/ErrorSnackbar/ErrorSnackbar';

interface BillContainerProps extends BaseContainerProps {
  selectedBill: Bill;
  selectedBillSplit?: BillSplit;
  customer: Customer;
  lastInvoiceDate?: InvoiceDate;

  createOrder: (order: CreateOrderRequest, bill: Bill, customer: Customer) => void;
  removeOrder: (order: Order, bill: Bill, customer: Customer) => void;
  updateOrders: (customer: Customer, bill: Bill, orders: Array<Order>) => void;
  selectBillSplit: (billSplit: BillSplit | undefined) => void
  createBillSplit: (bill: Bill) => void;
  addOrderToSelectedBillSplit: (order: Order) => void;
  removeOrderFromBillSplit: (order: Order) => void;
  charge: (invoice: Invoice, paymentType: PaymentMethodType) => Promise<void>;
  createInvoice: (bill: Bill, orders: Order[], customer: Customer) => Promise<Invoice>;
  loadLastInvoiceDate: (customer: Customer) => void;
}

interface BillContainerState extends BaseContainerState {
  selectedOrders: Array<Order>;
  selectedUsers: Array<User>;
  orderPaymentStatusFilter: OrderPaymentStatusFilter;
  printers: Array<PrinterInterface>;
}

class BillContainer extends BaseContainer<BillContainerProps, BillContainerState> {

  constructor(props: BillContainerProps) {
    super(props,
      {
        selectedOrders: props.selectedBill ? BillService.getPendingPaymentOrders(props.selectedBill) : [],
        selectedUsers: [],
        orderPaymentStatusFilter: OrderPaymentStatusFilter.TOTAL,
        errorDialogOpened: false,
        printers: loadPrinterState(props.customer).printers
      });
  }

  componentDidMount() {
    this.props.loadLastInvoiceDate(this.props.customer);
  }

  componentDidUpdate(previousProps) {
    if (previousProps.selectedBill && this.props.selectedBill && SerializationUtils.getTime(previousProps.selectedBill.updated) != SerializationUtils.getTime(this.props.selectedBill.updated)) {
      this.setState({ selectedOrders: this.props.selectedBill ? BillService.getPendingPaymentOrders(this.props.selectedBill) : [] });
    }
  }

  render() {
    return (<>
      <BillView
        customer={this.props.customer}
        selectedBill={this.props.selectedBill}
        selectedBillSplit={this.props.selectedBillSplit}
        selectedOrders={this.state.selectedOrders}
        clearOrdersFilters={this.clearOrdersFilters.bind(this)}
        filterByOrders={this.filterByOrders.bind(this)}
        goToLocations={() => this.goTo(AppRoute.LOCATIONS)}
        onAddOrder={this.addOrder.bind(this)}
        onRemoveOrder={this.removeOrder.bind(this)}
        onUpdateOrders={this.updateOrders.bind(this)}
        onChangeIssueStatus={this.changeIssueStatus.bind(this)}
        onSelectBillSplit={this.selectBillSplit.bind(this)}
        onAddOrderToSplit={this.addOrderToBillSplit.bind(this)}
        onRemoveOrderFromSplit={this.removeOrderToBillSplit.bind(this)}
        charge={this.charge.bind(this)}
        printTicket={this.printInvoice.bind(this)}
        printKitchenOrders={this.printKitchenOrders.bind(this)}
        openCashDrawer={this.openCashDrawer.bind(this)}
        createBillSplit={this.createBillSplit.bind(this)}
        lastInvoiceDate={this.props.lastInvoiceDate}
        loadProduct={this.loadProduct.bind(this)}
      />
      <InvoiceDateClosedDialog open={this.props.lastInvoiceDate == undefined || this.props.lastInvoiceDate!.status == "CLOSED"}
        onClick={() => { this.props.history.push(AppRoute.CONFIG) }} />
      <ErrorSnackbar show={this.state.errorDialogOpened} error={this.props.error} onClose={this.onClearError.bind(this)} />
    </>);
  }

  goTo(route: AppRoute) {
    browserHistory.push(route);
  }

  filterByOrders(orders: Array<Order>) {
    this.setState({ selectedOrders: orders });
  }

  clearOrdersFilters() {
    this.setState({ selectedOrders: this.props.selectedBill ? BillService.getPendingPaymentOrders(this.props.selectedBill) : [], selectedUsers: [] });
  }

  addOrder(product: Product) {
    if (this.props.selectedBill) {
      if (product.type !== ProductType.GROUP) {
        var order = {
          productId: product.id
        } as CreateOrderRequest;
        this.props.createOrder(order, this.props.selectedBill, this.props.customer);
      } else {
        // This needs to use the Group Configurer, so force the waiter to go to Products instead
        browserHistory.push(AppRoute.TAKE_ORDER);
      }
    }
  }

  updateOrders(orders: Array<Order>) {
    this.props.updateOrders(this.props.customer, this.props.selectedBill, orders);
  }

  changeIssueStatus(orders: Array<Order>, issueStatus: IssueStatus) {
    if (issueStatus == IssueStatus.ISSUED) {
      orders.filter(o => o.issueStatus != IssueStatus.ISSUED).forEach(o => o.issueStatus = issueStatus);
    } else {
      orders.forEach(o => o.issueStatus = issueStatus);
    }
    this.props.updateOrders(this.props.customer, this.props.selectedBill, orders);
  }

  removeOrder(order: Order) {
    this.props.removeOrder(order, this.props.selectedBill, this.props.customer);
  }

  charge(paymentMethod: PaymentMethodType) {
    if (this.props.selectedBill) {
      if (!this.props.selectedBill.invoice) {
        this.props.createInvoice(this.props.selectedBill, this.state.selectedOrders, this.props.customer)
          .then(invoice => {
            this.props.charge(invoice, paymentMethod)
              .then(() => {
                this.setState({ selectedOrders: [] });
                browserHistory.push(AppRoute.TAKE_ORDER);
              });
          });
      } else {
        this.props.charge(this.props.selectedBill.invoice, paymentMethod)
          .then(() => {
            this.setState({ selectedOrders: [] });
            browserHistory.push(AppRoute.TAKE_ORDER);
          });
      }
    }
  }

  createBillSplit() {
    if (this.props.selectedBill) {
      this.props.createBillSplit(this.props.selectedBill);
    } else {
      browserHistory.push(AppRoute.LOCATIONS);
    }
  }

  selectBillSplit(billSplit: BillSplit | undefined) {
    if (billSplit) {
      this.filterByOrders(billSplit.orders);
    }
    this.props.selectBillSplit(billSplit);
  }

  addOrderToBillSplit(order: Order) {
    this.props.addOrderToSelectedBillSplit(order);
    this.filterByOrders(this.props.selectedBillSplit!.orders);
  }

  removeOrderToBillSplit(order: Order) {
    this.props.removeOrderFromBillSplit(order);
    this.filterByOrders(this.props.selectedBillSplit!.orders);
  }

  loadProduct(productId: string) {
    return loadFullProduct(this.props.customer, productId)
  }


  printInvoice() {
    if (this.props.selectedBill && this.props.selectedBill.orders.length > 0) {
      if (this.props.selectedBill.invoice) {
        this.printTicket(this.props.selectedBill.invoice);
      } else {
        this.props.createInvoice(this.props.selectedBill, this.state.selectedOrders, this.props.customer)
          .then(invoice => this.printTicket(invoice));
      }
    }
  }

  printTicket(invoice: Invoice) {
    var pendingOrders = BillService.getPendingPaymentOrders(this.props.selectedBill);
    var orderGroups = BillService.listOrderGroups(pendingOrders).map(og => this.asThermalPrinterOrderGroup(og));
    var phones = Utils.joinAsString(this.props.customer.phones!, ",");
    var location = Utils.joinAsString(this.props.selectedBill.locations.map(l => l.description), ",");
    this.state.printers.filter(printer => printer.targets.indexOf('bill') != -1)
      .forEach(printer => {
        var billData = {
          customerName: this.props.customer.name,
          customerCif: this.props.customer.cif ? this.props.customer.cif : "",
          customerPhones: phones ? phones : "",
          customerAddress: this.props.customer.address!,
          location: location ? location : "",
          waiter: "TODO",
          invoiceId: invoice.id + "",
          orderGroups: orderGroups
        };
        printBill(printer, billData);
      });
  }

  printKitchenOrders() {
    var location = Utils.joinAsString(this.props.selectedBill.locations.map(l => l.description), ",");
    this.state.printers.filter(printer => printer.targets.indexOf('kitchen') != -1)
      .forEach(printer => printKitchenOrders(printer, {
        location: location ? location : "",
        orders: this.asThermalPrinterKitchenOrderGroup(BillService.getPendingPaymentOrders(this.props.selectedBill)),
        waiter: "Camarero", // TODO
      }));
  }

  openCashDrawer() {
    this.state.printers.filter(printer => printer.targets.indexOf('bill') != -1)
      .forEach(printer => openCashDrawer(printer));
  }

  asThermalPrinterOrderGroup(og: any) {
    return {
      productName: og.product.name,
      quantity: og.orders.length,
      basePrice: og.product.basePrice,
      tax: og.product.tax
    }
  }

  asThermalPrinterKitchenOrderGroup(orders: Order[]) {
    var map = new Map<string, KitchenOrder[]>();
    orders.forEach(o => {
      var serviceTime = o.serviceTime!;
      var kitchenOrder = {
        productName: o.product.name,
        notes: o.notes ? o.notes : ""
      };
      if (map.has(serviceTime)) {
        map.get(serviceTime)!.push(kitchenOrder);
      } else {
        map.set(serviceTime, [kitchenOrder]);
      }
    });
    return map;
  }
}

function mapStateToProps(state: AppState): BillContainerProps {
  return baseMapStateToProps(state, {
    selectedBill: state.billState.selectedBill,
    selectedBillSplit: state.billState.selectedSplit,
    customer: state.billState.customer,
    lastInvoiceDate: state.invoicesState.lastInvoiceDate,
    route: AppRoute.BILL
  } as BillContainerProps);
}

function mapDispatchToProps(dispatch: Dispatch<AnyAction>) {
  return baseMapDispatchToProps(dispatch,
    {
      createOrder: createOrderActionCreator,
      removeOrder: removeOrderActionCreator,
      updateOrders: updateOrdersActionCreator,
      charge: chargeActionCreator,
      createBillSplit: createBillSplitActionCreator,
      selectBillSplit: selectBillSplitActionCreator,
      addOrderToSelectedBillSplit: addOrderToSelectedBillSplitActionCreator,
      removeOrderFromBillSplit: removeOrderFromSelectedBillSplitActionCreator,
      createInvoice: createInvoiceDraftActionCreator,
      loadLastInvoiceDate: getLastInvoiceDateCreator,
    }
  );
}

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(BillContainer);
