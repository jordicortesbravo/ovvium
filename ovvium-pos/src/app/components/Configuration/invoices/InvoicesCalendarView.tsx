import * as React from 'react';
import '../../../../assets/css/calendar/Calendar.css'
import * as style from './style.css';
import * as classNames from 'classnames';
import Calendar, { ViewCallbackProperties } from 'react-calendar';
import { InvoiceDatePage } from 'app/model/InvoiceDatePage';
import { Utils } from 'app/utils/Utils';
import { Row } from 'react-bootstrap';
import { Tile } from 'app/components/Tile/Tile';
import { InvoiceDate } from 'app/model/InvoiceDate';
import * as moment from 'moment';
import { Typography } from '@material-ui/core';
import MuiAlert from '@material-ui/lab/Alert';

interface InvoicesCalendarViewProps {
    selectedDate: Date,
    maxDate: Date,
    selectedInvoiceDate?: InvoiceDate;
    lastInvoiceDate?: InvoiceDate;
    selectedInvoiceDates?: InvoiceDatePage;
    onDateChange: (date: Date) => void
    onClickOpen: () => void;
    onClickClose: () => void;
}

interface InvoicesCalendarViewState {

}

export class InvoicesCalendarView extends React.Component<InvoicesCalendarViewProps, InvoicesCalendarViewState> {


    constructor(props) {
        super(props);
    }

    render() {
        let invoiceDates = this.props.selectedInvoiceDates?.content ?? []
        return <div>
            <Typography align="center" variant="h6">Fecha de Facturación</Typography>
            <Calendar
                className={style.calendar}
                value={this.props.selectedDate}
                maxDate={this.props.maxDate}
                onActiveStartDateChange={this.onViewChange.bind(this)}
                onChange={this.onDateChange.bind(this)}
                tileClassName={this.getDateClassname(invoiceDates)}
            />
            <Row className='w-100'>
                <Tile value="Cerrar" onClick={this.props.onClickClose.bind(this)} className={classNames(style.buttonGroupTile, style.closed)} disabled={this.isClosedDisabled()} />
                <Tile value="Abrir" onClick={this.props.onClickOpen.bind(this)} className={classNames(style.buttonGroupTile, style.open)} disabled={this.isOpenDisabled()} />
            </Row>
            {this.props.lastInvoiceDate?.status == "OPEN" && !this.isTodayInvoiceDate() && <MuiAlert className={style.warning} severity="warning">La fecha de facturación actual no es a fecha de hoy.</MuiAlert>}
        </div>
    }

    private isOpenDisabled(): boolean | undefined {
        if (this.props.selectedInvoiceDate?.status == "OPEN") {
            return true;
        }
        if (this.props.lastInvoiceDate?.status == "OPEN") {
            return true;
        }
        // Disable if it exists a greater invoice date 
        let maxInvoiceDate = moment(Utils.parseDate(this.props.lastInvoiceDate?.date) ?? new Date(0)).startOf("day")
        let selectedMoment = moment(this.props.selectedDate).startOf("day");
        if(selectedMoment.isBefore(maxInvoiceDate)) {
            return true;
        }
        return false;
    }

    private isClosedDisabled(): boolean | undefined {
        if (!this.props.selectedInvoiceDate || this.props.selectedInvoiceDate.status == "CLOSED") {
            return true;
        }
        return false;
    }

    private getDateClassname(invoiceDates: InvoiceDate[]) {
        return ({ date, view }) => {
            if (view === 'month') {
                let current = invoiceDates.find(it => {
                    let idDate = Utils.parseDate(it.date);
                    return idDate!.getDate() == date.getDate() &&
                        idDate!.getMonth() == date.getMonth()
                });
                if (current) {
                    return current.status == "OPEN" ? style.open : style.closed;
                }
            }
            return null;
        };
    }

    private onViewChange(viewCallback: ViewCallbackProperties) {
        if (viewCallback.view == "month") {
            this.props.onDateChange(viewCallback.activeStartDate)
        }
    }

    private onDateChange(date: Date | Date[]) {
        let selectedDate = Array.isArray(date) ? date[0] : date;
        this.props.onDateChange(selectedDate)
    }

    private isTodayInvoiceDate() {
        return moment(Utils.parseDate(this.props.lastInvoiceDate?.date)).startOf("day").isSame(moment(new Date).startOf("day"))
    }

}