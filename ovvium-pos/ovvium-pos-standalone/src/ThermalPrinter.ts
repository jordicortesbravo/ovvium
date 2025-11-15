import Console from 'escpos/adapter/console';
import Network from 'escpos/adapter/network';
import USB from 'escpos/adapter/usb';
import Printer from 'escpos/printer';
import PromiseSocket from "promise-socket";

const net = require('net');

export enum PrinterConnector {
    USB = "USB", 
    NETWORK = "NETWORK", 
    CONSOLE = "CONSOLE"
}

export interface PrinterInterface {
    id: string;
    connectionData?: string;
    name: string;
    type: PrinterConnector;
    targets: Array<'bill' | 'kitchen'>;
}

export interface OrderGroup{
    productName: string;
    quantity: number;
    basePrice: number;
    tax: number;
}

export interface BillData {
    customerName: string;
    customerCif: string;
    customerAddress: string;
    customerPhones: string;
    waiter: string;
    location: string;
    invoiceId: string;
    orderGroups: OrderGroup[];
}

//La key es el ServiceTime
export interface KitchenData {
    location: string;
    orders: Map<string, KitchenOrder[]>;
    waiter: string;
}

export interface KitchenOrder {
    productName: string;
    notes: string;
}

export class ThermalPrinter implements PrinterInterface {

    id: string;
    name: string;
    connectionData?: string;
    type: PrinterConnector;
    targets: Array<'bill' | 'kitchen'>;

    constructor(printerInterface: PrinterInterface) {
        this.id = printerInterface.id;
        this.name = printerInterface.name;
        this.connectionData = printerInterface.connectionData;
        this.type = printerInterface.type;
        this.targets = printerInterface.targets;
    }

    printTest() {
        var {device, printer} = this.printerDevice();
        device.open(() => {
            printer
                .newLine()
                .newLine()
                .drawLine()
                .println("Test de impresion OK!")
                .drawLine()
                .newLine()
                .newLine()
                .newLine()
                .newLine()
                .newLine()
                .cut()
                .close();
        });
    }

    printBill(billData: BillData) {

        var {device, printer} = this.printerDevice();
        device.open(() => {
            this.printBillHeader(printer, billData);
            var totalBasePrice = 0;
            var totalTaxPrice = 0;
            var totalPrice = 0;
            billData.orderGroups.forEach(orderGroup => {
                var orderBasePrice = orderGroup.basePrice;
                var orderTaxPrice =orderBasePrice * orderGroup.tax;;
                totalBasePrice += orderBasePrice;
                totalTaxPrice += orderTaxPrice;
                totalPrice += orderBasePrice + orderTaxPrice;
                var orderPrice = orderBasePrice + orderTaxPrice;
                printer.tableCustom([
                    {text: orderGroup.quantity, align: 'RIGHT', width: 0.07},
                    {text: "", align: 'RIGHT', width: 0.01},
                    {text: orderGroup.productName, align: 'LEFT', width: 0.47},
                    {text: orderPrice.toFixed(2), align: 'RIGHT', width: 0.20},
                    {text: (orderPrice * orderGroup.quantity).toFixed(2), align: 'RIGHT', width: 0.20},
                ]);
            });
            printer
                .newLine()
                .drawLine()
                .tableCustom([
                    {text: "BASE IMPONIBLE", align: 'RIGHT', width: 0.60},
                    {text: totalBasePrice.toFixed(2), align: 'RIGHT', width: 0.30},
                ])
                .tableCustom([
                    {text: "IVA 10%", align: 'RIGHT', width: 0.60},
                    {text: totalTaxPrice.toFixed(2), align: 'RIGHT', width: 0.30},
                ])
                .drawLine()
                .style('B').size(2,2).align('CT')
                .tableCustom([
                    {text: "TOTAL", align: 'RIGHT', width: 0.60},
                    {text: totalPrice.toFixed(2), align: 'RIGHT', width: 0.30},
                ])
                .style('NORMAL').size(1,1);
                this.printBillFooter(printer);
          });
    }

    openCashDrawer() {
        var {device, printer} = this.printerDevice();
        device.open(() => printer.cashdraw().cashdraw().close());
    }

    printKitchenOrders(kitchenData: KitchenData) {
        var {device, printer} = this.printerDevice();
        device.open(() => {
            printer.drawLine()
            .align('LT').style('B').size(2,2)
            .println(kitchenData.location)
            .style('NORMAL').size(1,1)
            .println("Atendido por: " + kitchenData.waiter)
            .drawLine()
            .newLine();

            var serviceTimes = ['SOONER', 'STARTER', 'FIRST_COURSE', 'SECOND_COURSE', 'DESSERT','OTHER']
            serviceTimes.forEach(serviceTime => {
                if(kitchenData.orders.has(serviceTime)) {
                    var orders = kitchenData.orders.get(serviceTime)!;
                    if(orders.length > 0) {
                        this.printOrderGroupsForKitchen(printer, orders, serviceTime);
                    }
                }
            });

            printer.newLine()
                .newLine()
                .newLine()
                .newLine()
                .cut()
                .close();
        });  
    }

    async checkStatus()  {
        try {
            if(this.type == PrinterConnector.USB) {
                new USB();
                this.name = 'Impresora térmica - USB';
                return true;
            } else if(this.type == PrinterConnector.NETWORK) {
                const client = new PromiseSocket(new net.Socket());
                client.setTimeout(500);
                var address = this.connectionData!.split(":")[0];
                var port: any = this.connectionData!.split(":")[1];
                if(!port) {
                    port = 9100;
                }
                try {
                    await client.connect(port, address);
                    return true;
                } finally {
                    client.destroy();
                }
            }
            return false;
        } catch(err) {
            return false;
        }
    }

    private printOrderGroupsForKitchen(printer: Printer, orders: KitchenOrder[], serviceTime: string) {
        if(orders.length != 0) {
            printer.println(this.getServiceTimeLabel(serviceTime).toUpperCase())
                .newLine();
            orders.forEach(order => {
                printer.tableCustom([
                    {text: orders.length, align: 'RIGHT', width: 0.07},
                    {text: "", align: 'RIGHT', width: 0.01},
                    {text: this.normalize(order.productName), align: 'LEFT', width: 0.87}
                ]);
            orders.forEach(order => {
                if(order.notes) {
                    printer.tableCustom([
                        {text: "", align: 'RIGHT', width: 0.10},
                        {text: "", align: 'RIGHT', width: 0.01},
                        {text: this.normalize(order.notes), align: 'LEFT', width: 0.87}
                    ]);
                }
                })
            });
            printer.newLine();
        }
    }

    private printBillHeader(printer: Printer, billData: BillData) {
        printer
            .style('B').size(2,2).align('CT')
            .println(this.normalize(billData.customerName))
            .style('NORMAL').size(1,1)
            .println(this.normalize(billData.customerAddress))
            .println("TEL: " + billData.customerPhones)
            .println("NIF/CIF: " + billData.customerCif)
            .println(new Date().toLocaleDateString() + " " + new Date().toLocaleTimeString())
            .newLine()
            .drawLine()
            .align('LT').style('B').size(2,2)
            .println(billData.location)
            .style('NORMAL').size(1,1)
            .newLine()
            .println('FACTURA SIMPLIFICADA: ' + billData.invoiceId)
            .drawLine()
            .tableCustom([
                {text: "UDS", align: 'RIGHT', width: 0.07},
                {text: "", align: 'RIGHT', width: 0.01},
                {text: "DESCRIPCION", align: 'LEFT', width: 0.47},
                {text: "PVP", align: 'RIGHT', width: 0.20},
                {text: "IMPORTE", align: 'RIGHT', width: 0.20},
            ])
            .newLine();
    }

    private printBillFooter(printer: Printer) {
        printer.newLine()
        .println("GRACIAS POR SU VISITA")
        .newLine()
        .cut()
        .close();
    }

    private printerDevice() {
        var device: USB | Network | Console;
        switch(this.type) {
            case PrinterConnector.USB:
                device = new USB();
                break;
            case PrinterConnector.NETWORK:
                var address = this.connectionData!.split(":")[0];
                var port = this.connectionData!.split(":")[1];
                if(!port) {
                    port = "3000";
                }
                device = new Network(address, port);
                break;
            case PrinterConnector.CONSOLE:
                device = new Console();
                break;
            default:
                throw new Error("Device type not supported by Ovvium");

        }
        var printer = new Printer(device);
        return {device, printer};
    }

    private getServiceTimeLabel(serviceTime: string) {
        switch(serviceTime) {
            case 'SOONER':
                return "Lo antes posible";
            case 'STARTER':
                return "Entrante";
            case 'FIRST_COURSE':
                return "Primer plato";
            case 'SECOND_COURSE':
                return "Segundo plato";
            case 'DESSERT': 
                return "Postre";
            case 'OTHER':
            default: 
                return "Otro";
        }
    }

    private normalize(s: string) {
        return s.normalize('NFD')
        .replace(/([^n\u0300-\u036f]|n(?!\u0303(?![\u0300-\u036f])))[\u0300-\u036f]+/gi,"$1")
        .normalize()
        .replace(/ñ/gi,"n");
    }
}
  