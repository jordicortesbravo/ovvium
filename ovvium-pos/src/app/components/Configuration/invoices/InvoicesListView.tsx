import * as React from 'react';
import * as style from './style.css';
import * as classNames from 'classnames';
import { InvoiceDate } from 'app/model/InvoiceDate';
import * as moment from 'moment';
import { InvoicePage } from 'app/model/InvoicePage';
import { PaginatedList } from 'app/components/PaginatedList/PaginatedList';
import { Invoice } from 'app/model/Invoice';
import { Row, Col } from 'react-bootstrap';
import { Utils } from 'app/utils/Utils';
import Chip from '@material-ui/core/Chip';
import EventIcon from '@material-ui/icons/Event';

interface InvoicesListViewProps {
    selectedDate?: Date;
    selectedInvoiceDate?: InvoiceDate;
    selectedInvoices?: InvoicePage;
    onNextPage: () => void;
    onPreviousPage: () => void;
    onClickInvoice: (invoice: Invoice) => void;
}

interface InvoicesListViewState {

}

export class InvoicesListView extends React.Component<InvoicesListViewProps, InvoicesListViewState> {


    constructor(props) {
        super(props);
    }

    render() {
        return <div className="h-100">
            <div className={classNames(style.date, this.getDateClassName())}>
                <p className={style.vcenter}>
                    {this.props.selectedDate != undefined ? moment(this.props.selectedDate).format("DD-MM-YYYY") : ''}
                </p>
            </div>
            {!this.props.selectedInvoiceDate && <p className={classNames(style.noInvoiceDate, style.vcenter)}>
                <span className="d-block">No existe aún fecha de facturación.</span>
                <EventIcon className={style.emptyInvoicesIcon} />
            </p>}
            {this.props.selectedInvoices &&
                <div className={style.listWrapper}>
                    <PaginatedList
                        page={this.props.selectedInvoices}
                        onPrevious={this.props.onPreviousPage.bind(this)}
                        onNext={this.props.onNextPage.bind(this)}
                        onItemClick={this.props.onClickInvoice.bind(this)}
                        children={<InvoiceItem />}
                        childClassName={style.invoiceItem} />
                </div>
            }
        </div>
    }

    private getDateClassName() {
        return this.props.selectedInvoiceDate != null ? style[this.props.selectedInvoiceDate.status.toLowerCase()] : style.base
    }


}


const InvoiceItem: React.FC = (props: any): React.ReactElement<Invoice> => {
    let invoice = props.item as Invoice;
    return (
        <Row className={style.invoiceItemRow}>
            <Col xs="3"  sm="3" lg="2">
                <p className={style.vcenter}>{invoice.invoiceNumber}</p>
            </Col>
            <Col lg="4" className="d-none d-lg-block">
                <p className={style.vcenter}>{invoice.orders.length} pedidos</p>
            </Col>
            <Col xs="6" sm="6" lg="3">
                <Chip className={classNames(style.paymentType, style.vcenter)} label={paymentTypeLabel(invoice)} variant="outlined" color="primary" />
            </Col>
            <Col xs="3" sm="3" lg="3">
                <p className={style.vcenter}>{Utils.formatPrice(invoice.totalAmount, invoice.currency)}</p>
            </Col>
        </Row>)
}

function paymentTypeLabel(invoice: Invoice): string {
    switch (invoice.paymentType) {
        case "CASH": return "EFECTIVO";
        case "CARD": return "TARJETA";
        default: return "APP";
    }
}

