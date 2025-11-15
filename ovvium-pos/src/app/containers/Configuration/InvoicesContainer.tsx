import { AppState } from 'app/store/AppState';
import * as React from 'react';
import { connect } from 'react-redux';
import { Col, Row } from 'react-bootstrap';
import * as classNames from 'classnames';
import * as style from './style.css';
import { AnyAction, bindActionCreators, Dispatch } from 'redux';
import { InvoicesCalendarView } from '../../components/Configuration/invoices/InvoicesCalendarView';
import { Customer } from 'app/model/Customer';
import { pageInvoiceDatesCreator, changeStatusInvoiceDateCreator, pageInvoicesCreator } from 'app/actions/InvoiceActions';
import { InvoiceDatePage } from './../../model/InvoiceDatePage';
import { InvoiceDate } from 'app/model/InvoiceDate';
import { InvoicesListView } from 'app/components/Configuration/invoices/InvoicesListView';
import { createInvoiceDateCreator, getLastInvoiceDateCreator } from './../../actions/InvoiceActions';
import { InvoicePage } from 'app/model/InvoicePage';
import { Utils } from 'app/utils/Utils';
import { Invoice } from 'app/model/Invoice';
import { InvoicePreviewView } from 'app/components/Configuration/invoices/InvoicePreviewView';


interface InvoicesContainerProps {
    customer: Customer;
    selectedInvoiceDates?: InvoiceDatePage;
    selectedInvoices?: InvoicePage;
    lastInvoiceDate?: InvoiceDate;
    loadInvoiceDates?: (customer: Customer, date: Date) => void;
    loadInvoices?: (customer: Customer, page: number, invoiceDate?: InvoiceDate) => void;
    loadLastInvoiceDate?: (customer: Customer) => void;
    changeStatusInvoiceDate?: (customer: Customer, invoiceDate: InvoiceDate, status: "OPEN" | "CLOSED") => void;
    createInvoiceDate?: (customer: Customer, date: Date) => void;
}


interface InvoicesContainerState {
    selectedDate: Date;
    currentPage: number;
    selectedInvoiceDate?: InvoiceDate;
    selectedInvoice?: Invoice;
}

class InvoicesContainer extends React.Component<InvoicesContainerProps, InvoicesContainerState> {


    constructor(props) {
        super(props);
        const lastInvoiceDate = props.lastInvoiceDate;
        this.state = {
            currentPage: 0,
            selectedDate: Utils.parseDate(lastInvoiceDate?.date) ?? new Date() // today
        };
    }

    render() {
        const today = new Date();
        return (<div className={style.mainWrapper}>
            <Row className="h-100">
                <Col xs="12" sm="12" md="4" lg="3" className={classNames("h-100", style.column)}>
                    <InvoicesCalendarView
                        selectedDate={this.state.selectedDate}
                        maxDate={today}
                        onDateChange={this.onDateChange.bind(this)}
                        selectedInvoiceDates={this.props.selectedInvoiceDates}
                        lastInvoiceDate={this.props.lastInvoiceDate}
                        selectedInvoiceDate={this.state.selectedInvoiceDate}
                        onClickOpen={this.onOpenSelectedInvoiceDate.bind(this)}
                        onClickClose={this.onCloseSelectedInvoiceDate.bind(this)}
                    />
                </Col>
                <Col xs="12" sm="12" md="4" lg="4" className={classNames("h-100", style.column)}>
                    <InvoicesListView
                        selectedDate={this.state.selectedDate}
                        selectedInvoiceDate={this.state.selectedInvoiceDate}
                        selectedInvoices={this.props.selectedInvoices}
                        onNextPage={this.onNextPage.bind(this)}
                        onPreviousPage={this.onPreviousPage.bind(this)}
                        onClickInvoice={this.onSelectInvoice.bind(this)}
                    />
                </Col>
                <Col xs="12" sm="12" md="4" lg="5" className={classNames("h-100", style.column)}>
                    <InvoicePreviewView
                        customer={this.props.customer}
                        invoice={this.state.selectedInvoice}
                    />
                </Col>
            </Row>
        </div>)
    }

    componentDidMount() {
        this.props.loadInvoiceDates!(this.props.customer, this.state.selectedDate)
        this.props.loadLastInvoiceDate!(this.props.customer)
    }


    // Mirar como tipar esto
    static getDerivedStateFromProps(props: any, state: any) {
        const tProps = props as InvoicesContainerProps;
        const tState = state as InvoicesContainerState;
        const newSelectedInvoiceDate = tProps.selectedInvoiceDates?.content?.find(id => Utils.parseDate(id.date)!.getDate() == tState.selectedDate.getDate());
        if (newSelectedInvoiceDate != tState.selectedInvoiceDate) {
            return { selectedInvoiceDate: newSelectedInvoiceDate }
        }
        return null
    }

    componentDidUpdate(prevProps: InvoicesContainerProps, prevState: InvoicesContainerState) {
        if (prevProps.selectedInvoiceDates !== this.props.selectedInvoiceDates) {
            this.props.loadLastInvoiceDate!(this.props.customer)
            this.refreshSelectedInvoiceDateState(this.state.selectedDate)
        }
        if (prevState.selectedInvoiceDate !== this.state.selectedInvoiceDate) {
            this.props.loadInvoices!(this.props.customer, this.state.currentPage, this.state.selectedInvoiceDate)
        }
    }

    private onOpenSelectedInvoiceDate() {
        if (this.state.selectedInvoiceDate != undefined) {
            this.props.changeStatusInvoiceDate!(this.props.customer, this.state.selectedInvoiceDate, "OPEN")
        } else {
            this.props.createInvoiceDate!(this.props.customer, this.state.selectedDate)
        }
    }

    private onCloseSelectedInvoiceDate() {
        if (this.state.selectedInvoiceDate != undefined) {
            this.props.changeStatusInvoiceDate!(this.props.customer, this.state.selectedInvoiceDate, "CLOSED")
        }
    }

    private onDateChange(date: Date) {
        if (date.getMonth() != this.state.selectedDate.getMonth()) {
            this.props.loadInvoiceDates!(this.props.customer, date)
        }
        this.refreshSelectedInvoiceDateState(date);
        this.setState({
            selectedDate: date,
            currentPage: 0,
            selectedInvoice: undefined
        })
    }

    private onNextPage() {
        const next = this.state.currentPage + 1;
        this.setState({ currentPage: next })
        this.props.loadInvoices!(this.props.customer, next, this.state.selectedInvoiceDate)
    }

    private onPreviousPage() {
        const previous = this.state.currentPage - 1;
        this.setState({ currentPage: previous })
        this.props.loadInvoices!(this.props.customer, previous, this.state.selectedInvoiceDate)
    }

    private onSelectInvoice(invoice: Invoice) {
        this.setState({
            selectedInvoice: invoice
        })
    }

    private refreshSelectedInvoiceDateState(date: Date): InvoiceDate | undefined {
        const selectedInvoiceDate = this.props.selectedInvoiceDates?.content?.find(id => Utils.parseDate(id.date)!.getDate() == date.getDate());
        this.setState({
            selectedInvoiceDate: selectedInvoiceDate
        })
        return selectedInvoiceDate
    }
}

function mapStateToProps(state: AppState): InvoicesContainerProps {
    return {
        selectedInvoiceDates: state.invoicesState.invoiceDates,
        selectedInvoices: state.invoicesState.invoices,
        lastInvoiceDate: state.invoicesState.lastInvoiceDate
    } as InvoicesContainerProps;
}

function mapDispatchToProps(dispatch: Dispatch<AnyAction>) {
    return bindActionCreators(
        {
            loadInvoiceDates: pageInvoiceDatesCreator,
            loadLastInvoiceDate: getLastInvoiceDateCreator,
            changeStatusInvoiceDate: changeStatusInvoiceDateCreator,
            createInvoiceDate: createInvoiceDateCreator,
            loadInvoices: pageInvoicesCreator
        },
        dispatch
    );
}

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(InvoicesContainer);
