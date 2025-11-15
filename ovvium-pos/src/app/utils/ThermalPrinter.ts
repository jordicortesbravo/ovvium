import { Order } from "app/model/Order";
import { OrderGroupChoice } from "app/model/OrderGroupChoice";

var thermalPrinterBridge;

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
    order?: Order;
    orderGroupChoice?: OrderGroupChoice;
    productName: string;
    notes: string;
}

export function printTest(pi: PrinterInterface) {
    thermalPrinter().printTest(pi);
}

export function printBill(pi: PrinterInterface, billData: BillData) {
    thermalPrinter().printBill(pi, billData);
}

export function printKitchenOrders(pi: PrinterInterface, kitchenData: KitchenData) {
    thermalPrinter().printKitchenOrders(pi, {
        ...kitchenData,
        //@ts-ignore
        orders:JSON.stringify([...kitchenData.orders])
    });
}

export function openCashDrawer(pi: PrinterInterface) {
    thermalPrinter().openCashDrawer(pi);
}

export function checkPrinterStatus(pi: PrinterInterface) {
    thermalPrinter().checkPrinterStatus(pi);
}

function thermalPrinter() {
    if(thermalPrinterBridge === undefined) {
        // @ts-ignore
        if(window.require !== undefined) {
            // @ts-ignore
            const { remote } = window.require('electron');
            thermalPrinterBridge = remote.require('./electron-starter.js');
        } else {
            thermalPrinterBridge = {
                printTest: (pi: PrinterInterface) => console.log("printTest:" + JSON.stringify(pi)),
                printBill: (pi: PrinterInterface, billData: BillData) => console.log("printBill:" + "[pi:" + JSON.stringify(pi) + "][billData:" + JSON.stringify(billData) +"]"),
                printKitchenOrders: (pi: PrinterInterface, kitchenData: KitchenData) => console.log("printBill:" + "[pi:" + JSON.stringify(pi) + "][kitchenData:" + JSON.stringify(kitchenData) +"]"),
                openCashDrawer: (pi: PrinterInterface) => console.log("openCashDrawer:" + JSON.stringify(pi)),
                checkPrinterStatus: (pi: PrinterInterface) => console.log("checkPrinterStatus:" + JSON.stringify(pi)),
            }
        }
    }
    return thermalPrinterBridge;
}