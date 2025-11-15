import { Bill } from 'app/model/Bill';
import { OrderPaymentStatusFilter } from 'app/model/enum/OrderPaymentStatusFilter';
import { PaymentMethodType } from 'app/model/enum/PaymentMethodType';
import { PaymentStatus } from 'app/model/enum/PaymentStatus';
import { InvoiceDate } from 'app/model/InvoiceDate';
import { Order } from 'app/model/Order';
import { Product } from 'app/model/Product';
import { BillService } from 'app/services/BillService';
import { LocationService } from 'app/services/LocationService';
import * as classNames from 'classnames';
import * as React from 'react';
import { Col, Row } from 'react-bootstrap';
import { Tile } from '../Tile/Tile';
import * as style from './style.css';

class OrderResume {
  product: Product;
  quantity: number;

  constructor(product: Product, quantity: number) {
    this.product = product;
    this.quantity = quantity;
  }

  incrementQuantity() {
    this.quantity += 1;
  }

  getTotalPrice() {
    return (this.product.price * this.quantity).toFixed(2) + '€';
  }
}

interface OrdersGridProps {
  bill?: Bill;
  selectedOrders: Array<Order>;
  lastInvoiceDate?: InvoiceDate;

  goToLocations: () => void;
  onClickTotalButton?: () => void;
  charge?: (paymentMethod: PaymentMethodType) => void;
}

interface OrderGridState {
  orderPaymentStatusFilter: OrderPaymentStatusFilter;
  paymentStep: 'initial' | 'pickPaymentMethod' | 'confirmPayment';
  paymentMethod?: PaymentMethodType;
}

export class OrdersGrid extends React.Component<OrdersGridProps, OrderGridState> {

  constructor(props: OrdersGridProps) {
    super(props);
    this.state = { orderPaymentStatusFilter: OrderPaymentStatusFilter.PENDING, paymentStep: 'initial' }
  }

  render() {
    var orders = this.props.bill && this.props.selectedOrders ? this.listOrderResume(this.props.selectedOrders) : [];
    return <div className='h-100 w-100'>
      <Tile
        value={LocationService.getLocationName(this.props.bill)}
        className={classNames('w-100',
          this.props.bill && this.props.bill.locations && this.props.bill.locations.length > 3 ? style.joinedLocations : '',
          style.locationButton,
          BillService.hasPendingIssueOrders(this.props.bill) ? style.pendingLocationButton : style.fineLocationButton)}
        onClick={this.props.goToLocations}
      />
      <Row className='w-100'>
        <Tile value="Pendiente" className={style.orderPaymentStatusFilterButton} selected={this.state.orderPaymentStatusFilter == OrderPaymentStatusFilter.PENDING} onClick={() => this.setState({ orderPaymentStatusFilter: OrderPaymentStatusFilter.PENDING })} />
        <Tile value="Pagado" className={style.orderPaymentStatusFilterButton} selected={this.state.orderPaymentStatusFilter == OrderPaymentStatusFilter.PAID} onClick={() => this.setState({ orderPaymentStatusFilter: OrderPaymentStatusFilter.PAID })} />
        <Tile value="Total" className={style.orderPaymentStatusFilterButton} selected={this.state.orderPaymentStatusFilter == OrderPaymentStatusFilter.TOTAL} onClick={() => this.setState({ orderPaymentStatusFilter: OrderPaymentStatusFilter.TOTAL })} />
      </Row>
      <div className={classNames('w-100', style.ordersGridContainer)}>
        <div className={style.ordersContainer}>
          <div className={style.innerOrdersContainer}>
            <Row className={style.ordersGridTitle}>
              <Col lg="1" className={style.orderResumeDetail}>Ct.</Col>
              <Col lg="8">Producto</Col>
              <Col lg="3" className={classNames(style.orderResumeDetail, style.orderResumeDetailPrice)}>Precio</Col>
            </Row>
            {orders.map(orderResume => this.renderOrder(orderResume))}
          </div>
        </div>
        {this.props.onClickTotalButton &&
          <Tile value={this.getPrice(orders).toFixed(2) + "€"} onClick={this.props.onClickTotalButton}
            className={classNames('w-100', style.chargeButtonNoColor)} />
        }
        {this.props.charge && this.state.paymentStep == 'initial' &&
          <Tile value={this.getPrice(orders).toFixed(2) + "€"} onClick={() => this.setState({ paymentStep: 'pickPaymentMethod' })}
            className={classNames('w-100', style.chargeButton)}>
            <span className={style.chargeButtonChargeText}>{"Cobrar "}</span>
          </Tile>
        }
        {this.state.paymentStep == 'pickPaymentMethod' &&
          <Row className='w-100'>
            <Tile value="Efectivo" className={classNames(style.chargeButtonRegular, style.firstchargeButtonRegular)} onClick={() => this.setState({ paymentMethod: PaymentMethodType.CASH, paymentStep: 'confirmPayment' })} />
            <Tile value="Tarjeta" className={style.chargeButtonRegular} onClick={() => this.setState({ paymentMethod: PaymentMethodType.CARD, paymentStep: 'confirmPayment' })} />
            <Tile value="Cancelar" className={classNames(style.chargeButtonRegular, style.chargeButtonCancel)} onClick={() => this.setState({ paymentStep: 'initial', paymentMethod: undefined })} />
          </Row>
        }
        {this.state.paymentStep == 'confirmPayment' &&
          <Row className='w-100'>
            <Tile value={"Cancelar cobro " + this.getPaymentMethodLabel()} className={classNames(style.chargeButtonRegular, style.chargeButtonCancel)} onClick={() => this.setState({ paymentStep: 'pickPaymentMethod', paymentMethod: undefined })} />
            <Tile value={"Cobrar: " + this.getPrice(orders).toFixed(2) + "€"} className={classNames(style.chargeButtonRegular, style.firstchargeButtonRegular, style.chargeButtonRegularX2, style.chargeButtonOk)} onClick={() => {
              this.props.charge!(this.state.paymentMethod!);
              this.setState({ paymentMethod: undefined, paymentStep: 'initial' });
            }} />
          </Row>
        }
      </div>
    </div>
  }

  private renderOrder(orderResume: OrderResume) {
    return <Row className={style.orderResumeRow} key={orderResume.product.id}>
      <Col lg="1" className={style.orderResumeDetail}>{orderResume.quantity}</Col>
      <Col lg="8">{orderResume.product.name}</Col>
      <Col lg="3" className={classNames(style.orderResumeDetail, style.orderResumeDetailPrice)}>{orderResume.getTotalPrice()}</Col>
    </Row>
  }

  // private confirmPayment() {
  //   this.props.charge!(this.state.paymentMethod!);
  //   this.setState({paymentMethod:undefined, paymentStep: 'initial'});
  // }

  private listOrderResume(orders: Array<Order>) {
    var map = new Map<string, OrderResume>();
    var filter = this.getOrderPaymentStatusToFilter(this.state.orderPaymentStatusFilter);
    orders.filter(filter).forEach(o => {
      var productId = o.product.id;
      if (map.has(productId)) {
        map.get(productId)!.incrementQuantity();
      } else {
        map.set(productId, new OrderResume(o.product, 1))
      }
    })
    return Array.from(map.values()).sort((og1, og2) => {
      return og1.product.name >= og2.product.name ? 1 : -1;
    });
  }

  private getOrderPaymentStatusToFilter(filter: OrderPaymentStatusFilter) {
    switch (filter) {
      case OrderPaymentStatusFilter.PENDING:
        return (o: Order) => [PaymentStatus.PENDING].lastIndexOf(o.paymentStatus) != -1;
      case OrderPaymentStatusFilter.PAID:
        return (o: Order) => [PaymentStatus.PAID].lastIndexOf(o.paymentStatus) != -1;
      case OrderPaymentStatusFilter.TOTAL:
        return (o: Order) => true;
    }
  }

  private getPrice(orders: Array<OrderResume>) {
    var price = 0;
    orders.forEach(o => price += o.quantity * o.product.price);
    return price;
  }

  private getPaymentMethodLabel() {
    if (this.state.paymentMethod == PaymentMethodType.CARD) {
      return " (Tarjeta)";
    } else if (this.state.paymentMethod == PaymentMethodType.CASH) {
      return " (Efectivo)"
    }
    return "";
  }
}
