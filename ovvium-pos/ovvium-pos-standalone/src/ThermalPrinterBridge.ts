import { BillData, KitchenData, PrinterInterface, ThermalPrinter } from "./ThermalPrinter";

export function printTest(pi: PrinterInterface) {
    printer(pi).printTest();
}

export function printBill(pi: PrinterInterface, billData: BillData) {
    printer(pi).printBill(billData);
}

export function printKitchenOrders(pi: PrinterInterface, kitchenData: KitchenData) {
    printer(pi).printKitchenOrders(kitchenData);
}

export function openCashDrawer(pi: PrinterInterface) {
    printer(pi).openCashDrawer();
}

export function checkPrinterStatus(pi: PrinterInterface) {
    printer(pi).checkStatus();
}

function printer(pi: PrinterInterface) {
    return new ThermalPrinter(pi);
}