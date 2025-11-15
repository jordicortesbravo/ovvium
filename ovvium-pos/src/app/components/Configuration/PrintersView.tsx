import { faCheck, faTimes } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Tile } from 'app/components/Tile/Tile';
import { PrinterConnector, PrinterInterface } from 'app/utils/ThermalPrinter';
import * as classNames from 'classnames';
import * as React from 'react';
import { Col, Form, Row } from 'react-bootstrap';
import { ConfirmButtons } from '../ConfirmButtons/ConfirmButtons';
import * as style from './style.css';

interface PrintersViewProps {
    printers: Array<PrinterInterface>;
    bindStatus?: 'ok' | 'ko';
    savePrinter: (printer: PrinterInterface) => void;
    removePrinter: (printer: PrinterInterface) => void;
    bindPrinter: (printer: PrinterInterface) => void;
    printTestText: (printer: PrinterInterface) => void;
    clearBindStatus: () => void;
}

interface PrintersViewState {
    currentView?: 'listPrinters' | 'editPrinter';
    currentPrinter?: PrinterInterface;
    bindStatus?: 'ok' | 'ko';
}

export class PrintersView extends React.Component<PrintersViewProps, PrintersViewState> {

    constructor(props) {
        super(props);
        this.state = { currentView: 'listPrinters', bindStatus: props.bindStatus };
    }

    componentDidUpdate() {
        if (this.props.bindStatus != this.state.bindStatus) {
            this.setState({ bindStatus: this.props.bindStatus });
        }
    }

    render() {
        return <div className={style.mainWrapper}>
            <Row className="h-100">
                <Col sm="10" className={classNames("h-100", style.printCol)}>
                    <div className={style.formContainer}>
                        {this.state.currentView == 'listPrinters' && this.props.printers.length == 0 &&
                            <h5 className={style.emptyPrintsMessage}>{"No hay ninguna impresora configurada"}</h5>}
                        {this.state.currentView == 'listPrinters' && this.props.printers.length > 0 &&
                            this.renderPrintersList()
                        }
                        {this.state.currentView == 'editPrinter' && this.renderAddPrinterForm()}
                    </div>
                    {this.state.currentView == 'editPrinter' &&
                        <Row className={style.buttonsContainer}>
                            <ConfirmButtons
                                onCancel={this.closeEditPrinter.bind(this)}
                                onAccept={this.savePrinter.bind(this)}
                            />
                        </Row>
                    }
                </Col>
                <Col xs="2" className={classNames(style.printCol, style.printButtonsContainer)}>
                    <div className={style.formContainer}>
                        <Row>
                            <Tile onClick={() => this.props.printTestText(this.state.currentPrinter!)} disabled={this.state.currentPrinter == undefined} className={style.tile} value="Impresión de prueba" />
                        </Row>
                        <Row>
                            <Tile disabled={this.state.currentPrinter == undefined} onClick={this.editCurrentPrinter.bind(this)} className={style.tile} value="Editar impresora" />
                        </Row>
                        <Row>
                            <Tile disabled={this.state.currentPrinter != undefined} onClick={this.addPrinter.bind(this)} className={style.tile} value="Nueva impresora" />
                        </Row>

                    </div>

                    <div className={style.removeButtonContainer}>
                        <Row>
                            <Tile disabled={this.state.currentPrinter == undefined} onClick={this.removeCurrentPrinter.bind(this)} className={style.tile} value="Eliminar impresora" />
                        </Row>
                    </div>
                </Col>
            </Row>
            <Row className={style.smallPrintButtonsContainer}>
                <Tile onClick={() => this.props.printTestText(this.state.currentPrinter!)} disabled={this.state.currentPrinter == undefined} className={style.tile} value="Impresión de prueba" />
                <Tile disabled={this.state.currentPrinter == undefined} onClick={this.editCurrentPrinter.bind(this)} className={style.tile} value="Editar impresora" />
                <Tile disabled={this.state.currentPrinter != undefined} onClick={this.addPrinter.bind(this)} className={style.tile} value="Nueva impresora" />
                <Tile disabled={this.state.currentPrinter == undefined} onClick={this.removeCurrentPrinter.bind(this)} className={style.tile} value="Eliminar impresora" />
            </Row>
        </div>
    }

    private renderPrintersList() {
        return this.props.printers.map(printer => <Tile key={'printer-' + printer.id} className={style.printerTile} value={printer.name}
            selected={this.state.currentPrinter && this.state.currentPrinter.id == printer.id} onClick={() => this.setState({ currentPrinter: printer })} />);
    }


    private renderAddPrinterForm() {
        return <Form.Group controlId="formBasicEmail" className={style.form}>
            <Row className='w-100'>
                <Tile value="USB" onClick={() => this.changeConnector(PrinterConnector.USB)} className={style.buttonGroupTile} selected={this.state.currentPrinter!.type == PrinterConnector.USB} />
                <Tile value="Red" onClick={() => this.changeConnector(PrinterConnector.NETWORK)} className={style.buttonGroupTile} selected={this.state.currentPrinter!.type == PrinterConnector.NETWORK} />
            </Row>
            <input placeholder="Nombre de la impresora" className={style.config} defaultValue={this.state.currentPrinter!.name}
                onChange={event => this.state.currentPrinter!.name = event.target.value} />
            {this.state.currentPrinter!.type == PrinterConnector.NETWORK &&
                <input placeholder="Dirección IP y puerto de la impresora (x.x.x.x:x)" className={style.config} defaultValue={this.state.currentPrinter!.connectionData}
                    onChange={event => this.state.currentPrinter!.connectionData = event.target.value} />}
            <Row className={classNames('w-100', style.spaceVertical10)}>
                <Tile value="Bar" onClick={() => this.switchTarget('bill')} className={style.buttonGroupTile} selected={this.state.currentPrinter!.targets.indexOf('bill') != -1} />
                <Tile value="Cocina" onClick={() => this.switchTarget('kitchen')} className={style.buttonGroupTile} selected={this.state.currentPrinter!.targets.indexOf('kitchen') != -1} />
            </Row>
            <Row>
                <Tile value="Comprobar conexión"
                    className={classNames(style.buttonGroupTile, style.spaceVertical10)} onClick={() => this.props.bindPrinter(this.state.currentPrinter!)} />
            </Row>
            <Row className={style.spaceVertical10}>
                {this.state.bindStatus != undefined && this.state.bindStatus == 'ok' &&
                    <div>
                        <FontAwesomeIcon icon={faCheck} color="green" size={"2x"} />
                        <span>{"Conexión correcta"}</span>
                    </div>
                }
                {this.state.bindStatus != undefined && this.state.bindStatus == 'ko' &&
                    <div>
                        <FontAwesomeIcon icon={faTimes} color="red" size={"2x"} />
                        <span>{"Fallo de conexión"}</span>
                    </div>
                }
            </Row>
        </Form.Group>
    }

    private addPrinter() {
        this.setState({
            currentView: 'editPrinter',
            currentPrinter: {
                id: '' + new Date().getTime(),
                type: PrinterConnector.USB,
                targets: ['bill']
            } as PrinterInterface
        });
    }

    private editCurrentPrinter() {
        this.setState({ currentView: 'editPrinter', currentPrinter: this.state.currentPrinter! });
    }

    private removeCurrentPrinter() {
        this.props.removePrinter(this.state.currentPrinter!);
        this.setState({ currentView: 'listPrinters', currentPrinter: undefined });
    }

    private savePrinter() {
        this.props.savePrinter(this.state.currentPrinter!);
        this.closeEditPrinter();
    }

    private closeEditPrinter() {
        this.setState({ bindStatus: undefined, currentPrinter: undefined, currentView: 'listPrinters' });
    }

    private switchTarget(target: 'bill' | 'kitchen') {
        var printer = this.state.currentPrinter!;
        if (printer.targets.indexOf(target) == -1) {
            printer.targets.push(target);
        } else {
            printer.targets = printer.targets.filter(t => t != target);
        }
        this.setState({ currentPrinter: Object.assign({}, printer) });
    }

    private changeConnector(connector: PrinterConnector) {
        var printer = this.state.currentPrinter!;
        printer.type = connector;
        this.setState({ currentPrinter: Object.assign({}, printer) });
        this.props.clearBindStatus();
    }
}