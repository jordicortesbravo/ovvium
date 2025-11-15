import { app, BrowserWindow, globalShortcut } from 'electron';
import { printTest, printBill, printKitchenOrders, openCashDrawer, checkPrinterStatus } from './ThermalPrinterBridge';

let mainWindow: Electron.BrowserWindow | null;

const POS_URL = "https://pos.ovvium.com";

function createWindow(): void {
    mainWindow = configureMainWindow();
    loadMainUrl();
    mainWindow.once('ready-to-show', () => {
        if (mainWindow) {
            mainWindow.show()
        }
    })

    mainWindow.on('closed', () => {
        mainWindow = null;
    });

    globalShortcut.register('CommandOrControl+R', () => {
        loadMainUrl();
    });
}


function configureMainWindow(): Electron.BrowserWindow {
    let window = new BrowserWindow({
        minWidth: 1280,
        minHeight: 760,
        height: 760,
        width: 1280,
        backgroundColor: '#6d6d6d',
        webPreferences: {
            nodeIntegration: true,
            nodeIntegrationInWorker: true,
            devTools: true
        },
        show: false
    });
    window.setMenu(null);
    return window;
}

function loadMainUrl() {
    if (mainWindow) {
        mainWindow.loadURL(POS_URL, { extraHeaders: "pragma: no-cache" });
    }
}



app.on('ready', createWindow);

app.on('window-all-closed', () => {
    if (process.platform !== 'darwin') {
        app.quit();
    }
});

app.on('activate', () => {
    if (mainWindow === null) {
        createWindow();
    }
});

app.on('will-quit', () => {
    globalShortcut.unregisterAll()
});


exports.printTest = printTest;
exports.printBill = printBill;
exports.printKitchenOrders = printKitchenOrders;
exports.openCashDrawer = openCashDrawer;
exports.checkPrinterStatus = checkPrinterStatus;

