import * as React from 'react';
import { Invoice } from 'app/model/Invoice';
import { Container, Row, Col } from 'react-bootstrap';
import * as style from './style.css';
import * as classNames from 'classnames';
import { Utils } from 'app/utils/Utils';
import { Customer } from 'app/model/Customer';
import { OrderInvoice } from 'app/model/OrderInvoice';


interface InvoicePreviewViewProps {
    invoice?: Invoice;
    customer: Customer;
}

interface OrderInvoiceRecord {
    productName: string;
    quantity: number;
    price: number;
    totalPrice: number
    tax: number;
    currency: string;
}

export class InvoicePreviewView extends React.Component<InvoicePreviewViewProps> {

    constructor(props) {
        super(props);
    }

    render() {
        const invoice = this.props.invoice;
        const customer = this.props.customer;
        const tax = (this.props.invoice?.orders[0].tax ?? 0) * 100;
        if (invoice)
            return (<div className={style.invoicePreview}>
                <div className={style.header}>
                    <Container className={classNames(style.invoiceSection, style.invoiceSeparator)}>
                        <Row>
                            <Col>{customer.name}</Col>
                        </Row>
                        <Row>
                            <Col>CIF {customer.cif}</Col>
                        </Row>
                        <Row>
                            <Col>{customer.address}</Col>
                        </Row>
                    </Container>
                    <Container className={classNames(style.invoiceSection, style.invoiceSeparator)}>
                        <Row>
                            <Col>Num Factura: {invoice.invoiceNumber}</Col>
                            <Col>{Utils.formatDate(invoice.creationDate, "DD-MM-YYYY HH:mm:ss")}</Col>
                        </Row>
                    </Container>
                    <Container className={classNames(style.invoiceSection)}>
                        <Row className={style.bold}>
                            <Col xs="1" lg="2" className={style.textLeft}>Uds</Col>
                            <Col className={style.textLeft}>Producto</Col>
                            <Col lg="3"  className={classNames(style.textRight, "d-none","d-lg-block")}>Precio</Col>
                            <Col xs="4" lg="3" className={style.textRight}>Importe</Col>
                        </Row>
                    </Container>
                </div>
                <Container className={classNames(style.invoiceSection, style.invoiceSeparator, style.orders)}>
                    {this.groupOrdersByName(invoice.orders).map(order => {
                        return (<Row className={style.orderItem}>
                            <Col xs="1" lg="2" className={style.textLeft}>{order.quantity}</Col>
                            <Col className={style.textLeft}>{order.productName}</Col>
                            <Col lg="3" className={classNames(style.textRight, "d-none","d-lg-block")}>{Utils.formatPrice(order.price, order.currency)}</Col>
                            <Col xs="4" lg="3" className={style.textRight}>{Utils.formatPrice(order.totalPrice, order.currency)}</Col>
                        </Row>)
                    })}
                </Container>
                <div className={style.footer}>
                    <Container className={classNames(style.invoiceSection, style.invoiceSeparator)}>
                        <Row>
                            <Col className={style.textRight}>Propina: {Utils.formatPrice(invoice.tipAmount, invoice.currency)}</Col>
                        </Row>
                    </Container>
                    <Container className={classNames(style.invoiceSection, style.invoiceSeparator)}>
                        <Row>
                            <Col md="6">IVA: {tax} %</Col>
                            <Col md="6">Base Imponible: {Utils.formatPrice(invoice.totalBaseAmount, invoice.currency)}</Col>
                        </Row>
                    </Container>
                    <Container className={style.invoiceSection}>
                        <Row className={classNames(style.total, style.bold)}>
                            <Col className={style.textRight}> Total (Imp Incl): {Utils.formatPrice(invoice.totalAmount, invoice.currency)}</Col>
                        </Row>
                    </Container>
                </div>
            </div>)
        return (<div />)
    }

    private groupOrdersByName(orders: OrderInvoice[]): OrderInvoiceRecord[] {
        const groupedOrders = Utils.groupBy(orders, it => it.productName);
        const orderRecords: OrderInvoiceRecord[] = []
        for (let name in groupedOrders) {
            let orders = groupedOrders[name];

            orderRecords.push({
                productName: name,
                quantity: orders.length,
                price: orders[0].price,
                totalPrice: orders.map(it => it.price).reduce((acc, curr) => acc + curr),
                tax: orders[0].tax,
                currency: orders[0].currency
            })
        }
        return orderRecords;
    }



}