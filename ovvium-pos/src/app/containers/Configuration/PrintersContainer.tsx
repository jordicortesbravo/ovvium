import { PrintersView } from 'app/components/Configuration/PrintersView';
import { Customer } from 'app/model/Customer';
import { AppState } from 'app/reducers';
import { ArrayUtils } from 'app/utils/ArrayUtils';
import { LocalStorageUtils } from 'app/utils/LocalStorageUtils';
import { checkPrinterStatus, PrinterConnector, PrinterInterface, printTest } from 'app/utils/ThermalPrinter';
import * as React from 'react';
import { connect } from 'react-redux';


interface PrinterContainerProps {
    customer: Customer;
}

interface PrintersContainerState {
    bindStatus?: 'ok' | 'ko';
    printers: Array<PrinterInterface>;
}
interface PrintersState {
    printers: Array<PrinterInterface>;
}

class PrintersContainer extends React.Component<PrinterContainerProps, PrintersContainerState> {

    constructor(props: PrinterContainerProps) {
        super(props);
        this.state = {
            printers: loadPrinterState(props.customer).printers
        };
    }

    render() {
        return <PrintersView
            bindPrinter={printer => checkPrinterStatus(printer)}
            bindStatus={this.state.bindStatus}
            printTestText={printer => printTest(printer)}
            removePrinter={this.removePrinter.bind(this)}
            clearBindStatus={() => this.setState({ bindStatus: undefined })}
            printers={this.state.printers} savePrinter={this.savePrinter.bind(this)} />
    }

    savePrinter(printer: PrinterInterface) {
        if (printer.type != PrinterConnector.NETWORK) {
            printer.connectionData = undefined;
        }
        let printersState = loadPrinterState(this.props.customer);
        ArrayUtils.replace(printersState.printers, printer, 'id');
        saveState(this.props.customer, printersState);
        this.setState({ printers: printersState.printers })
    }

    removePrinter(printer: PrinterInterface) {
        let printersState = loadPrinterState(this.props.customer);
        ArrayUtils.remove(printersState.printers, printer, 'id');
        saveState(this.props.customer, printersState);
        this.setState({ printers: printersState.printers })
    }

}


export function loadPrinterState(customer: Customer): PrintersState {
    let printersText = LocalStorageUtils.get(customer.id + "_printers");
    let printersState: PrintersState;
    if (printersText) {
        printersState = JSON.parse(printersText) as PrintersState;
    } else {
        printersState = { printers: [] } as PrintersState;
    }
    return printersState;
}

export function saveState(customer: Customer, printersState: PrintersState) {
    LocalStorageUtils.add(customer.id + "_printers", JSON.stringify(printersState))
}

function mapStateToProps(state: AppState): PrinterContainerProps {
    return {
        customer: state.billState.customer
    } as PrinterContainerProps;
}


export default connect(
    mapStateToProps
)(PrintersContainer);
